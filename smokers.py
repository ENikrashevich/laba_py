"""
Simulation of the three smokers problem using threads and semaphores.

Three smokers (each with one of the components: tobacco, paper, matches)
and a bartender who places two components on the table. A smoker with
a third missing component can take the components from the table and roll a cigarette.
"""

import threading
import random
import time
import sys

class ComponentStore:
    """
    Centralized storage of components and semaphores for synchronization.

    Attributes:
        pairs (dict): A dictionary of semaphores for each pair of components
        bartender_lock (Semaphore): A semaphore for bartender access control
        ingredient_names (dict): Matching the keys of the pairs and their names
    """
    def __init__(self):
        """Initialization of the component storage."""
        self.pairs = {
            'TP': threading.Semaphore(0),  # Табак + Бумага
            'TM': threading.Semaphore(0),  # Табак + Спички
            'PM': threading.Semaphore(0)   # Бумага + Спички
        }
        self.bartender_lock = threading.Semaphore(1)
        self.ingredient_names = {
            'TP': ('Табак', 'Бумагу'),
            'TM': ('Табак', 'Спички'),
            'PM': ('Бумагу', 'Спички')
        }
    
    def get_pair_semaphore(self, pair_key):
        """
        Get a semaphore for a specific pair of components.

        Params:
            pair_key (str): The key of the component pair ('TP', 'TM' or 'PM')

        Returns:
            Semaphore: The semaphore corresponding to the requested pair
        """
        return self.pairs.get(pair_key)
    
    def get_random_pair(self):
        """
       Randomly select an available pair of components.

        Returns:
            str: A random key from the available component pairs
        """
        return random.choice(list(self.pairs.keys()))
    
    def get_ingredients(self, pair_key):
        """
        Get the names of the components for the specified pair.

        Parameters:
            pair_key (str): The key of the component pair ('TP', 'TM' or 'PM')

        Returns:
            tuple: A tuple with two component names
        """
        return self.ingredient_names.get(pair_key, ('', ''))


class Bartender(threading.Thread):
    """
    Stream is the bartender who places the ingredients on the table.

    Constructor Params:
        component_store (ComponentStore): Shared component storage

    Attributes:
        store (Component Store): Link to the component store
    """
    def __init__(self, component_store):
        """
        Initialization of the bartender's flow.

        Params:
            component_store (ComponentStore): Shared component storage
        """
        super().__init__(daemon=True)
        self.store = component_store
    
    def run(self):
        """The main work cycle of the bartender."""
        while True:
            # Ожидать разрешения на размещение компонентов
            self.store.bartender_lock.acquire()
            
            # Выбрать и разместить случайную пару
            selected = self.store.get_random_pair()
            item1, item2 = self.store.get_ingredients(selected)
            print(f"\033[94mБармен разместил: {item1} и {item2}\033[0m")
            
            # Сигнализировать о наличии компонентов
            self.store.get_pair_semaphore(selected).release()


class Smoker(threading.Thread):
    """
    A stream is a smoker waiting for components and smoking.

    Constructor Params:
        name (str): The smoker's name
        owned (str): A component owned by a smoker
        wait_for (Semaphore): The semaphore of the expected pair of components
        needs (tuple): A tuple of two necessary components
        component_store (ComponentStore): Shared component storage

    Attributes:
        name (str): The smoker's name
        owned (str): An existing component
        wait_sem (Semaphore): The semaphore of the expected pair
        needs (tuple): Necessary components
        store (Component Store): Link to the storage
    """
    def __init__(self, name, owned, wait_for, needs, component_store):
        """
        Initialization of the smoker's flow.

        Params:
            name (str): The smoker's name
            owned (str): A component owned by a smoker
            wait_for (Semaphore): The semaphore of the expected pair of components
            needs (tuple): A tuple of two necessary components
            component_store (ComponentStore): Shared component storage
        """
        super().__init__(daemon=True)
        self.name = name
        self.owned = owned
        self.wait_sem = wait_for
        self.needs = needs
        self.store = component_store
    
    def consume(self):
        """The process of smoking a cigarette."""
        print(f"\033[93m{self.name} начинает курить...\033[0m")
        delay = random.uniform(1.5, 4.5)
        time.sleep(delay)
        print(f"\033[93m{self.name} закончил ({delay:.1f} сек)\033[0m")
    
    def run(self):
        """The main work cycle of a smoker."""
        while True:
            # Ожидать нужные компоненты
            self.wait_sem.acquire()
            
            # Взять компоненты со стола
            print(f"\033[92m{self.name} (имеет {self.owned}) "
                  f"берёт {self.needs[0]} и {self.needs[1]}\033[0m")
            
            # Разрешить бармену положить новые компоненты
            self.store.bartender_lock.release()
            
            # Процесс курения
            self.consume()


if __name__ == "__main__":
    # Инициализация хранилища компонентов
    comp_store = ComponentStore()

    # Создание и запуск бармена
    bartender = Bartender(comp_store)
    bartender.start()

    # Конфигурация курильщиков
    smokers_config = [
        ("Курильщик Т", "Табак", comp_store.pairs['PM'], ("Бумагу", "Спички")),
        ("Курильщик Б", "Бумагу", comp_store.pairs['TM'], ("Табак", "Спички")),
        ("Курильщик С", "Спички", comp_store.pairs['TP'], ("Табак", "Бумагу"))
    ]

    # Создание и запуск курильщиков
    smokers = [
        Smoker(name, owned, sem, needs, comp_store)
        for name, owned, sem, needs in smokers_config
    ]

    for smoker in smokers:
        smoker.start()

    # Обработка прерывания Ctrl+C
    try:
        # Бесконечное ожидание с периодической проверкой
        while all(smoker.is_alive() for smoker in smokers) and bartender.is_alive():
            time.sleep(0.5)
    except KeyboardInterrupt:
        print("\nЗавершение работы по запросу пользователя")
        sys.exit(0)
