import zmq
import zmq.asyncio
import pickle
from plantProcessor import PlantProcessor
from plantRecipe import PlantRecipe
from weatherProvider import WeatherProvider
from reportMessage import ReportMessage

from messageCache import MessageCache


class ControlMessageHandler(MessageCache):
    __socket = None
    __weather_provider: WeatherProvider

    def __init__(self, weather_provider: WeatherProvider=None) -> None:
        super().__init__(None)

        if weather_provider is None:
            weather_provider = WeatherProvider()

        self.__weather_provider = weather_provider

        # TODO We need some kind of pub-sub here
        # Doing REQ-REP to an Edge does not really make any sense here

    async def process_message(self, message: bytearray) -> None:
        await self.generate_control_message(pickle.loads(message))

    async def generate_control_message(self, message: ReportMessage):
        plant_processor = PlantProcessor(self.__weather_provider, PlantRecipe("Unknown"))

        report = str(await plant_processor.water_status(message.humidity)) + str(
            await plant_processor.uv_status(message.uv))

        # TODO Submit the report here somehow
        print("Would have sent report: " + report)
        return report
