import asyncio
import signal
from core.databaseConnector import DatabaseConnector
from core.config import Config
from handler.edgeIDGenerator import EdgeIDGenerator
from handler.sensorSubmitter import SensorSubmitter
from server.serverIDReceiver import ServerIDReceiver
from server.serverFogReceiver import ServerFogReceiver



def main():
    signal.signal(signal.SIGINT, signal.SIG_DFL)
    loop = asyncio.get_event_loop()

    config = Config(loop)
    database_connector = DatabaseConnector(config)

    edge_id_generator = EdgeIDGenerator(database_connector)
    server_id_receiver = ServerIDReceiver(edge_id_generator)
    loop.create_task(server_id_receiver.recv_and_process())

    sensor_submitter = SensorSubmitter(database_connector)
    loop.create_task(sensor_submitter.process_loop())
    server_fog_receiver = ServerFogReceiver(sensor_submitter)
    loop.create_task(server_fog_receiver.recv_and_process())

    loop.run_forever()
    loop.close()


if __name__ == "__main__":
    main()
