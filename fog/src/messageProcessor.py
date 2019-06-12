from reportMessage import ReportMessage

class MessageProcessor:

    async def process_message(self, message: str) -> str:
        try:
            parsed_message = ReportMessage(message)
        except ValueError:
            return "Error parsing Message"


        print(parsed_message)

        return '1'
