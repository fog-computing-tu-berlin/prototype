# General Structure
- Edge: redet immer nur mit Fog
- Fog: relayed IDs for Edge; Caches Reports for Cloud; Sends out Control to Edge mit einem Ticker oder on Report bases
- Cloud ZMQ: Caches Reports & inserts into DB; Generates edge_id for Edge (replayed over fog)
- Cloud DB: Rest API vor simple DataObjects in `sensor` and `edge_id`

# Fog & Cloud
- Build with Python around ZerMQ (pyzmq)
- Build with asyncio to cope with Single Thread issue
    - Multiple Subroutines including Receiver with async-await: Ticker; Cache-Cleaner; General-Watcher for Server
        - Subroutines should be restart on Crash
        - Cache Collector in fog/src/control/controlSubmitterHolder.py
        - Each one Server in cloud/src/server on Fog & Cloud
        - Message Cache Duplicates in src/core/messageCache.py
        - Config specifices in both: src/core/config.py
        - Non-Async cache in fog/src/weather/weatherProvider.py
        - Bulk Cloud Uploader in fog/src/cloud/cloudUploadHandler.py
        - Async-Ticker in fog/src/control/controlSubmitter.py
    - All Subroutines use the default loop
        - The Server are started in the index.py & all other start themselfs on publish or Caches on init
    - Go through code and write a lot here
- Internal Message Cache with PUSH & PULL on both sides
    - Consumed by async-loop for everything
    - Abstract to be extentable
- 2 Bulk loops; Collecting for 2 Seconds & bulk submitting
    - Done for DB & Fog -> Cloud report sending
- If socket start to through to many errors (e. g. Timeout even though timeout was not reached) get killed and restarted
- Weather Calculation is done in Fog with OpenWeatherMap
    - Forecast in 3h steps
    - Debug Configurable
- Build with Docker

# Cloud Database
- Build without real security in mind
    - Just a POC simple REST Datastore
- 2 Tables:
    - sensor for report values
    - edge_id to avoid conflicting ids
- Build with docker

SEE ALL CONFIG SETTINGS