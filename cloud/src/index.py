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
    signal.signal(signal.SIGTERM, signal.SIG_DFL)
    loop = asyncio.get_event_loop()

    config = Config()
    database_connector = DatabaseConnector(config)

    edge_id_generator = EdgeIDGenerator(database_connector)
    server_id_receiver = ServerIDReceiver(config, edge_id_generator)
    server_id_receiver_loop = loop.create_task(server_id_receiver.recv_and_process())

    sensor_submitter = SensorSubmitter(config, database_connector)
    server_fog_receiver = ServerFogReceiver(config, sensor_submitter)
    server_fog_receiver_loop = loop.create_task(server_fog_receiver.recv_and_process())

    while True:
        try:
            loop.run_until_complete(asyncio.sleep(2))
            if server_id_receiver_loop.done():
                server_id_receiver_loop = loop.create_task(server_id_receiver.recv_and_process())
            if server_fog_receiver_loop.done():
                server_fog_receiver_loop = loop.create_task(server_fog_receiver.recv_and_process())
        except KeyboardInterrupt:
            break
        except:
            pass

    loop.close()


if __name__ == "__main__":
    main()
