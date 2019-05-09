package org.sunbird.cassandra;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.ColumnDefinition;
import org.apache.cassandra.db.Clustering;
import org.apache.cassandra.db.ClusteringPrefix;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.DeletionTime;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.CompositeType;
import org.apache.cassandra.db.marshal.ListType;
import org.apache.cassandra.db.marshal.MapType;
import org.apache.cassandra.db.marshal.SetType;
import org.apache.cassandra.db.partitions.Partition;
import org.apache.cassandra.db.rows.Cell;
import org.apache.cassandra.db.rows.CellPath;
import org.apache.cassandra.db.rows.Row;
import org.apache.cassandra.db.rows.Unfiltered;
import org.apache.cassandra.db.rows.UnfilteredRowIterator;
import org.apache.cassandra.triggers.ITrigger;
import org.sunbird.common.audit.AuditUtil;

public class Trigger implements ITrigger {

  private static final String OBJECT_TYPE = "objectType";
  private static final String OPERATION_TYPE = "operationType";
  private static final String UPDATE_ROW = "UPSERT";
  private static final String DELETE_ROW = "DELETE";
  private static final String FILE_TO_WRITE;
  private static ObjectMapper mapper = new ObjectMapper();

  static {
    String filePath = System.getProperty("sunbird_cassandra_audit_file_path");
    if (filePath == null) {
      filePath = "/var/log/cassandra/triggerAuditLog.log";
    }
    FILE_TO_WRITE = filePath;
  }

  @Override
  public Collection<Mutation> augment(Partition update) {
    Map<String, Object> resultMap = processEvent(update);
    Map<String, Object> auditEventMap = AuditUtil.getAuditEvent(resultMap);
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(FILE_TO_WRITE, true));
      out.write(mapper.writeValueAsString(auditEventMap) + "\n");
    } catch (IOException e) {
      System.out.println(
          "Trigger:augment: IOException occurred with error message = " + e.getMessage());
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          System.out.println(
              "Trigger:augment: Exception occured with error message = " + e.getMessage());
        }
      }
    }
    return null;
  }

  private Map<String, Object> getPartitionKeyData(ByteBuffer keyValueBuffer, CFMetaData metadata) {
    List<ColumnDefinition> partitionKeyColumns = metadata.partitionKeyColumns();
    Map<String, Object> partitionKeyValueMap = new HashMap<>();

    // This is only for handling partition key of size = 1
    if (partitionKeyColumns.size() == 1) {

      AbstractType<?> pkColumnType = partitionKeyColumns.get(0).type;
      Object pkValue = pkColumnType.compose(keyValueBuffer);

      if (pkValue != null) {
        partitionKeyValueMap.put(partitionKeyColumns.get(0).name.toString(), pkValue);
      }
    } else {
      // This is handling composite partition key. Cassandra have CompositeType class for handling
      // this
      for (int index = 0; index < partitionKeyColumns.size(); index++) {
        String pkColumnName = partitionKeyColumns.get(index).name.toString();
        AbstractType<?> pkColumnType = partitionKeyColumns.get(index).type;

        ByteBuffer valueBuffer = CompositeType.extractComponent(keyValueBuffer, index);
        Object pkValue = pkColumnType.compose(valueBuffer);

        if (pkValue != null) {
          partitionKeyValueMap.put(pkColumnName, pkValue);
        }
      }
    }
    return partitionKeyValueMap;
  }

  private Map<String, Object> getClusterKeyData(Partition update, Unfiltered next) {
    Map<String, Object> clusterKeyValueMap = new HashMap<>();

    List<ColumnDefinition> clusteringColumns = update.metadata().clusteringColumns();
    ClusteringPrefix clustering = next.clustering();

    for (int index = 0; index < clustering.size(); index++) {
      ColumnDefinition columnDefinition = clusteringColumns.get(index);

      String columnName = columnDefinition.name.toString();
      AbstractType<?> columnType = columnDefinition.type;

      Object ckValue = columnType.compose(clustering.get(index));
      clusterKeyValueMap.put(columnName, ckValue);
    }

    return clusterKeyValueMap;
  }

  public Map<String, Object> processEvent(Partition partition) {
    try {
      DecoratedKey partitionKey = partition.partitionKey();

      Map<String, Object> partitionKeyData =
          getPartitionKeyData(partitionKey.getKey(), partition.metadata());

      UnfilteredRowIterator unfilteredIterator = partition.unfilteredIterator();

      // Is delete operation?
      DeletionTime levelDeletion = partition.partitionLevelDeletion();
      if (!levelDeletion.isLive()) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put(OPERATION_TYPE, DELETE_ROW);
        eventMap.put(OBJECT_TYPE, partition.metadata().cfName);
        eventMap.putAll(partitionKeyData);
        return eventMap;
      }
      while (unfilteredIterator.hasNext()) {
        Unfiltered next = unfilteredIterator.next();
        Map<String, Object> clusterKeyData = getClusterKeyData(partition, next);

        ClusteringPrefix clustering = next.clustering();
        Row row = partition.getRow((Clustering) clustering);

        Iterable<Cell> cells = row.cells();

        Map<String, Object> eventMap = new HashMap<>();
        Map<Object, String> updateColumnCollectionInfo = new HashMap<>();
        Map<Object, Object> deletedDataMap = new HashMap<>();
        for (Cell cell : cells) {
          AbstractType<?> columnType = getColumnType(cell);
          if (columnType instanceof MapType<?, ?>) {
            processMapDataType(eventMap, updateColumnCollectionInfo, cell);
          } else if (columnType instanceof SetType<?>) {
            processSetDataType(eventMap, deletedDataMap, cell);
          } else if (columnType instanceof ListType<?>) {
            processListDataType(eventMap, updateColumnCollectionInfo, cell);
          } else {
            String columnName = getColumnName(cell);
            if (cell.isLive(0)) {
              eventMap.put(columnName, getCellValue(cell));
            } else {
              eventMap.put(columnName, null);
            }
          }
        }
        eventMap.put(OPERATION_TYPE, UPDATE_ROW);
        eventMap.put(OBJECT_TYPE, partition.metadata().cfName);
        eventMap.putAll(partitionKeyData);
        eventMap.putAll(clusterKeyData);
        return eventMap;
      }
    } catch (RuntimeException e) {
      System.out.println(
          "Trigger:processEvent: RuntimeException occurred with error message = " + e.getMessage());
    }

    return null;
  }

  private void processMapDataType(
      Map<String, Object> dataMap, Map<Object, String> updateColumnCollectionInfo, Cell cell) {
    String columnName = getColumnName(cell);
    AbstractType<?> columnType = getColumnType(cell);
    Object cellValue = getCellValue(cell);
    MapType<Object, Object> mapType = (MapType<Object, Object>) columnType;

    AbstractType<Object> keysType = mapType.getKeysType();
    CellPath path = cell.path();
    int size = path.size();
    for (int i = 0; i < size; i++) {
      ByteBuffer byteBuffer = path.get(i);
      Object cellKey = keysType.compose(byteBuffer);
      Map<Object, Object> map = new HashMap<Object, Object>();
      if (!dataMap.containsKey(columnName)) {
        dataMap.put(columnName, map);
      } else {
        map = (Map<Object, Object>) dataMap.get(columnName);
      }
      if (cell.isLive(0)) {
        map.put(cellKey.toString(), cellValue);
      } else {
        if (!updateColumnCollectionInfo.containsKey(columnName)) {
          updateColumnCollectionInfo.put(columnName, columnType.getClass().getName());
        }
        map.put(cellKey.toString(), null);
      }
    }
  }

  private void processSetDataType(
      Map<String, Object> dataMap, Map<Object, Object> deletedDataMap, Cell cell) {
    String columnName = getColumnName(cell);
    Object cellValue = getCellValue(cell);
    AbstractType<?> columnType = getColumnType(cell);
    MapType<Object, Object> mapType = (MapType<Object, Object>) columnType;
    CellPath path = cell.path();
    int size = path.size();
    for (int i = 0; i < size; i++) {
      ByteBuffer byteBuffer = path.get(i);
      AbstractType<Object> keysType = ((SetType) columnType).getElementsType();
      cellValue = keysType.compose(byteBuffer);
    }
    if (cell.isLive(0)) {
      if (!dataMap.containsKey(columnName)) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.add(cellValue);
        dataMap.put(columnName, arrayList);
      } else {
        ArrayList<Object> arrayList = (ArrayList<Object>) dataMap.get(columnName);
        if (!arrayList.contains(cellValue)) {
          arrayList.add(cellValue);
        }
      }
    } else {
      if (!deletedDataMap.containsKey(columnName)) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.add(cellValue);
        deletedDataMap.put(columnName, arrayList);
      } else {
        ArrayList<Object> arrayList = (ArrayList<Object>) deletedDataMap.get(columnName);
        if (!arrayList.contains(cellValue)) {
          arrayList.add(cellValue);
        }
      }
    }
  }

  private void processListDataType(
      Map<String, Object> dataMap, Map<Object, String> updateColumnCollectionInfo, Cell cell) {
    String columnName = getColumnName(cell);
    Object cellValue = getCellValue(cell);
    AbstractType<?> columnType = getColumnType(cell);
    updateColumnCollectionInfo.put(columnName, columnType.getClass().getName());
    if (cell.isLive(0)) {
      if (!dataMap.containsKey(columnName)) {
        ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.add(cellValue);
        dataMap.put(columnName, arrayList);
      } else {
        ArrayList<Object> arrayList = (ArrayList<Object>) dataMap.get(columnName);
        if (!arrayList.contains(cellValue)) {
          arrayList.add(cellValue);
        }
      }
    }
  }

  private String getColumnName(Cell cell) {
    return cell.column().name + "";
  }

  private Object getCellValue(Cell cell) {
    return ((AbstractType<Object>) cell.column().cellValueType()).compose(cell.value());
  }

  private AbstractType<?> getColumnType(Cell cell) {
    return cell.column().type;
  }
}
