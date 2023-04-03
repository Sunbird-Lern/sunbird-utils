CREATE KEYSPACE IF NOT EXISTS sunbird_programs WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': '1'
 };


CREATE TABLE IF NOT EXISTS sunbird_programs.program_enrollment (
     program_id text,
     program_externalId text,
     program_name text,
     pii_consent_required boolean,
     user_id text,
     user_locations map<text, text>,
     organisation_id text,
     organisation_name text,
     user_sub_type text,
     user_type text,
     created_at date,
     updated_at date,
     PRIMARY KEY (program_id,user_id)
     ) WITH bloom_filter_fp_chance = 0.01
         AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
         AND comment = ''
         AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
         AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
         AND crc_check_chance = 1.0
         AND dclocal_read_repair_chance = 0.1
         AND default_time_to_live = 0
         AND gc_grace_seconds = 864000
         AND max_index_interval = 2048
         AND memtable_flush_period_in_ms = 0
         AND min_index_interval = 128
         AND read_repair_chance = 0.0
         AND speculative_retry = '99PERCENTILE';

