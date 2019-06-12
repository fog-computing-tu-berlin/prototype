from reportMessage import ReportMessage
from plantProcessor import PlantProcessor
from weatherProvider import WeatherProvider
from plantRecipe import PlantRecipe


class MessageProcessor:
    def __init__(self) -> None:
        super().__init__()

    async def process_message(self, message: str) -> str:
        try:
            parsed_message = ReportMessage(message)
        except ValueError:
            return "Error parsing Message"

        plant_processor = PlantProcessor(WeatherProvider(), PlantRecipe("Unknown"))

        report = str(await plant_processor.water_status(parsed_message.humidity)) + str(await plant_processor.uv_status(parsed_message.uv))

        return report
