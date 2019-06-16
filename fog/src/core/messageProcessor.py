from cloud.cloudUploadHandler import CloudUploaderHandler
from control.controlMessageHandler import ControlMessageHandler
from core.reportMessage import ReportMessage


class MessageProcessor:
    cloud_upload_handler: CloudUploaderHandler
    control_message_handler: ControlMessageHandler

    def __init__(self, cloud_upload_handler: CloudUploaderHandler, control_message_handler: ControlMessageHandler) -> None:
        super().__init__()
        self.cloud_upload_handler = cloud_upload_handler
        self.control_message_handler = control_message_handler

    async def process_message(self, message: str) -> str:
        try:
            parsed_message = ReportMessage(message)
        except ValueError:
            return "Error parsing Message"

        await self.control_message_handler.publish(parsed_message)
        await self.cloud_upload_handler.publish(parsed_message)

        # 1 -> Confirm the received message
        return '1'
