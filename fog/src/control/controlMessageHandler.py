import pickle
from plant.plantProcessor import PlantProcessor
from plant.plantRecipe import PlantRecipe
from weather.weatherProvider import WeatherProvider
from core.reportMessage import ReportMessage

from core.messageCache import MessageCache
from control.controlSubmitterHolder import ControlSubmitterHolder


class ControlMessageHandler(MessageCache):
    __socket = None
    __weather_provider: WeatherProvider

    def __init__(self, control_submitter_holder: ControlSubmitterHolder, weather_provider: WeatherProvider = None) -> None:
        super().__init__(None)

        self.control_submitter_holder = control_submitter_holder

        if weather_provider is None:
            weather_provider = WeatherProvider()
        self.__weather_provider = weather_provider

    async def process_message(self, message: bytearray) -> None:
        report_message = pickle.loads(message)
        control_message = await self.generate_control_message(report_message)

        print("Add report to queue: " + control_message)
        await self.control_submitter_holder.create_or_update_last_report_message(report_message.edge_id, control_message)

    async def generate_control_message(self, message: ReportMessage):
        plant_processor = PlantProcessor(self.__weather_provider, PlantRecipe("Unknown"))

        report = str(await plant_processor.water_status(message.humidity)) + str(
            await plant_processor.uv_status(message.uv))

        return report

