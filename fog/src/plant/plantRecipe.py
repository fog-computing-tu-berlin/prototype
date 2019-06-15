DEFAULT_HUMIDITY = 50.0


class PlantRecipe:
    name: str

    def __init__(self, name: str) -> None:
        super().__init__()
        self.name = name

    def target_humidity(self) -> float:
        humidity_switcher = {
            "potato": 20.0,
            "carrot": 30.0,
            "basil": 40.0,
        }
        return humidity_switcher.get(self.name, DEFAULT_HUMIDITY)
