package org.sunbird.cassandra;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.ColumnDefinition;
import org.apache.cassandra.db.Clustering;
import org.apache.cassandra.db.ClusteringPrefix;
import org.apache.cassandra.db.DecoratedKey;
import org.apache.cassandra.db.DeletionTime;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.PartitionColumns;
import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.CompositeType;
import org.apache.cassandra.db.marshal.ListType;
import org.apache.cassandra.db.marshal.MapType;
import org.apache.cassandra.db.marshal.SetType;
import org.apache.cassandra.db.partitions.Partition;
import org.apache.cassandra.db.rows.Cell;
import org.apache.cassandra.db.rows.CellPath;
import org.apache.cassandra.db.rows.Row;
import org.apache.cassandra.db.rows.Row.Deletion;
import org.apache.cassandra.db.rows.Unfiltered;
import org.apache.cassandra.db.rows.UnfilteredRowIterator;
import org.apache.cassandra.triggers.ITrigger;

/**
 * @author iostream04
 *
 */
public class Trigger implements ITrigger {

  private static final String KEYSPACE = "keyspace";
  private static final String TABLE = "table";
  private static final String OPERATION_TYPE = "operationType";
  private static final String UPDATE_ROW = "UPDATE_ROW";
  private static final String DELETE_ROW = "DELETE_ROW";
  private static final String UPDATE_COUNTER = "UPDATE_COUNTER";
  private static final String FILE_TO_WRITE = "";

  @Override
  public Collection<Mutation> augment(Partition update) {

    HashMap<Object, Object> result = processData(update);
    try {

      BufferedWriter out = new BufferedWriter(new FileWriter(FILE_TO_WRITE, true));
      out.write(result + "\n");
      out.write("-------" + "\n");
      out.close();
    } catch (IOException e) {
      System.out.println("ExceptionOccured");
    }

    return null;
  }

  private HashMap<Object, Object> getPartitionKeyData(ByteBuffer key, CFMetaData update) {

    List<ColumnDefinition> partitionKeyList = new ArrayList<ColumnDefinition>();
    HashMap<Object, Object> partitionKeyValueList = new HashMap<Object, Object>();
    Object dataObject = null;
    partitionKeyList = update.partitionKeyColumns();
    if (partitionKeyList.size() == 1) {

      AbstractType<?> ptype = partitionKeyList.get(0).type;
      dataObject = ptype.compose(key);

      if (dataObject != null) {
        partitionKeyValueList.put(partitionKeyList.get(0).name.toString(), dataObject);
      }
    } else {
      for (int pkIndex = 0; pkIndex < partitionKeyList.size(); pkIndex++) {
        String pname = partitionKeyList.get(pkIndex).name.toString();
        AbstractType<?> ptype = partitionKeyList.get(pkIndex).type;

        ByteBuffer tempKey = CompositeType.extractComponent(key, pkIndex);
        dataObject = ptype.compose(tempKey);

        if (dataObject != null) {
          partitionKeyValueList.put(pname, dataObject);
        }
      }
    }

    return partitionKeyValueList;
  }

  private Map<String, Object> getClusterKeyData(Partition update, Unfiltered next) {
    List<ColumnDefinition> clusterKeyList = new ArrayList<ColumnDefinition>();
    Map<String, Object> clusterKeyValueList = new HashMap<String, Object>();

    clusterKeyList = update.metadata().clusteringColumns();
    ClusteringPrefix clustering = next.clustering();
    int clSize = clustering.size();
    for (int i = 0; i < clSize; i++) {
      ColumnDefinition columnDefinition = clusterKeyList.get(i);
      String columnName = columnDefinition.name.toString();
      AbstractType<?> columnType = columnDefinition.type;
      Object compose = columnType.compose(clustering.get(i));
      clusterKeyValueList.put(columnName, compose);
    }
    return clusterKeyValueList;
  }

  public HashMap<Object, Object> processData(Partition partition) {
    String updateType = null;

    try {
      final String table = partition.metadata().cfName;
      final String keyspace = partition.metadata().ksName;
      HashMap<Object, Object> partitionKeyData = new HashMap<Object, Object>();
      Map<String, Object> clusterKeyData = new HashMap<String, Object>();
      DecoratedKey partitionKey = partition.partitionKey();
      partitionKeyData = getPartitionKeyData(partitionKey.getKey(), partition.metadata());

      DeletionTime levelDeletion = partition.partitionLevelDeletion();

      if (!levelDeletion.isLive()) {

        updateType = DELETE_ROW;
        partitionKeyData.put(OPERATION_TYPE, updateType);
        partitionKeyData.put(TABLE, table);
        partitionKeyData.put(KEYSPACE, keyspace);
        return partitionKeyData;
      }

      UnfilteredRowIterator unfilteredIterator = partition.unfilteredIterator();
      while (unfilteredIterator.hasNext()) {
        updateType = UPDATE_ROW;

        Unfiltered next = unfilteredIterator.next();

        ClusteringPrefix clustering = next.clustering();
        Row row = partition.getRow((Clustering) clustering);
        clusterKeyData = getClusterKeyData(partition, next);

        Iterable<Cell> cells = row.cells();

        final Map<Object, String> updateColumnCollectionInfo = new HashMap<Object, String>();
        HashMap<Object, Object> dataMap = new HashMap<Object, Object>();
        HashMap<Object, Object> deletedDataMap = new HashMap<Object, Object>();

        Deletion deletion = row.deletion();

        if (deletion != null && !deletion.isLive()) {
          updateType = DELETE_ROW;
        } else {

          for (Cell cell : cells) {

            ColumnDefinition column = cell.column();
            String columnName = column.name + "";
            // AbstractType<?> type = column.type;
            AbstractType<Object> cellValueType = (AbstractType<Object>) column.cellValueType();
            Object cellValue = cellValueType.compose(cell.value());
            AbstractType<?> columnType = column.type;
            if (cell.isCounterCell()) {
              updateType = UPDATE_COUNTER;
              dataMap.put(columnName, cellValue);
            } else if (columnType instanceof MapType<?, ?>) {
              MapType<Object, Object> mapType = (MapType<Object, Object>) columnType;

              AbstractType<Object> keysType = mapType.getKeysType();
              CellPath path = cell.path();
              int size = path.size();
              for (int i = 0; i < size; i++) {
                ByteBuffer byteBuffer = path.get(i);
                Object cellKey = keysType.compose(byteBuffer);

                if (!dataMap.containsKey(columnName)) {
                  Map<Object, Object> map = new HashMap<Object, Object>();
                  dataMap.put(columnName, map);

                  if (cell.isLive(0)) {
                    map.put(cellKey.toString(), cellValue);
                  } else {
                    if (!updateColumnCollectionInfo.containsKey(columnName)) {
                      updateColumnCollectionInfo.put(columnName, columnType.getClass().getName());
                    }
                    map.put(cellKey.toString(), null);
                  }
                } else {
                  Map<Object, Object> map = (Map<Object, Object>) dataMap.get(columnName);

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
            } else if (columnType instanceof SetType<?>) {
              CellPath path = cell.path();
              int size = path.size();
              for (int i = 0; i < size; i++) {
                ByteBuffer byteBuffer = path.get(i);
                AbstractType<Object> keysType = ((SetType) columnType).getElementsType();
                cellValue = keysType.compose(byteBuffer);

              }
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
            } else if (columnType instanceof ListType<?>) {
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
            } else {
              if (cell.isLive(0)) {
                dataMap.put(columnName, cellValue);
              } else {
                dataMap.put(columnName, null);
              }
            }
          }
        }
        dataMap.put(OPERATION_TYPE, updateType);
        dataMap.put(TABLE, table);
        dataMap.put(KEYSPACE, keyspace);
        dataMap.putAll(partitionKeyData);
        dataMap.putAll(clusterKeyData);
        return dataMap;

      }
    } catch (RuntimeException e) {

    }

    return null;
  }

}
