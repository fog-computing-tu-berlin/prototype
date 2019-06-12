import pyowm


class MessageProcessor:
    owm = pyowm.OWM('d1b343df8e5e1067b5b28d4272cca01f')

    def __init__(self, owm=None):
        if owm is not None:
            self.owm = owm

    async def get_weather_for_berlin(self):
        #return await self.owm.three_hours_forecast_at_coords(52.5, 13.4)
        return self.owm.three_hours_forecast("Berlin")

    async def process_message(self, msg):
        currentWeatherForcast = await self.get_weather_for_berlin()
        rain = currentWeatherForcast.when_rain()
        clear = currentWeatherForcast.when_clear()

        willHaveSun = currentWeatherForcast.will_have_sun()
        print('rain', rain)
        print('clear', clear)
        print('will_have_sun', willHaveSun)

        # Just an example for now
        return {"water": {"status": "notEnough", "amount": 5.25},
                "light": {"status": "OK"}}