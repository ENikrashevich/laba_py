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
from typing import Dict, Tuple, List, Optional


class ComponentStore:
    """
    Component storage with semaphores for synchronization.

    Attributes:
        pairs: A dictionary of semaphores for each pair of components
        bartender_lock: Semaphore for bartender access control
        component_names: Matching the keys of the pairs and their names
    """

    def __init__(self) -> None:
        """Initializing the component storage."""
        self.pairs: Dict[str, threading.Semaphore] = {
            'TP': threading.Semaphore(0),  # Tobacco + Paper
            'TM': threading.Semaphore(0),  # Tobacco + Matches
            'PM': threading.Semaphore(0)  # Paper + Matches
        }
        self.bartender_lock: threading.Semaphore = threading.Semaphore(1)
        self.ingredient_names: Dict[str, Tuple[str, str]] = {
            'TP': ('Tobacco', 'Paper'),
            'TM': ('Tobacco', 'Matches'),
            'PM': ('Paper', 'Matches')
        }

    def get_pair_semaphore(self, pair_key: str) -> Optional[threading.Semaphore]:
        """
        Get a semaphore for a specific pair of components.

        Params:
            pair_key: The key of the component pair ('TP', 'TM' or 'PM')

        Returns:
            The semaphore corresponding to the requested pair
        """
        return self.pairs.get(pair_key)

    def get_random_pair(self) -> str:
        """
        Randomly select an available pair of components.

        Returns:
            A random key from the available component pairs
        """
        return random.choice(list(self.pairs.keys()))

    def get_ingredients(self, pair_key: str) -> Tuple[str, str]:
        """
        Get the names of the components for the specified pair.

        Params:
            pair_key: The key of the component pair ('TP', 'TM' or 'PM')

        Returns:
            A tuple with two component names
        """
        return self.ingredient_names.get(pair_key, ('', ''))


class Bartender(threading.Thread):
    """
    Stream is the bartender who places the ingredients on the table.

    Constructor Params:
        component_store: Shared Component Storage
    """

    def __init__(self, component_store: ComponentStore) -> None:
        """
        Initialization of the bartender's flow.

        Params:
            component_store: Shared Component Storage
        """
        super().__init__(daemon=True)
        self.store: ComponentStore = component_store

    def run(self) -> None:
        """The main work cycle of the bartender."""
        while True:
            # Wait for permission to place components
            self.store.bartender_lock.acquire()

            # Select and place random pair
            selected: str = self.store.get_random_pair()
            item1, item2 = self.store.get_ingredients(selected)
            print(f"\033[94mBartender placed: {item1} and {item2}\033[0m")

            # Signal component availability
            semaphore = self.store.get_pair_semaphore(selected)
            if semaphore:
                semaphore.release()


class Smoker(threading.Thread):
    """
    A stream is a smoker waiting for components and smoking.

    Constructor Params:
        name: The smoker's name
        owned: A component owned by a smoker
        wait_for: The semaphore of the expected pair of components
        needs: A tuple of two necessary components
        component_store: Shared Component Storage
    """

    def __init__(
            self,
            name: str,
            wait_for: threading.Semaphore,
            needs: Tuple[str, str],
            component_store: ComponentStore
    ) -> None:
        """
        Initialization of the smoker's flow.

        Params:
            name: The smoker's name
            owned: A component owned by a smoker
            wait_for: The semaphore of the expected pair of components
            needs: A tuple of two necessary components
            component_store: Shared Component Storage
        """
        super().__init__(daemon=True)
        self.name: str = name
        self.wait_sem: threading.Semaphore = wait_for
        self.needs: Tuple[str, str] = needs
        self.store: ComponentStore = component_store

    def consume(self) -> None:
        """The process of smoking a cigarette."""
        print(f"\033[93m{self.name} starts smoking...\033[0m")
        delay: float = random.uniform(1.5, 4.5)
        time.sleep(delay)
        print(f"\033[93m{self.name} finished ({delay:.1f} sec)\033[0m")

    def run(self) -> None:
        """The main work cycle of a smoker."""
        while True:
            # Wait for needed components
            self.wait_sem.acquire()

            # Take components from table
            print(f"\033[92m{self.name} "
                  f"takes {self.needs[0]} and {self.needs[1]}\033[0m")

            # Allow bartender to place new components
            self.store.bartender_lock.release()

            # Smoking process
            self.consume()


def main() -> None:
    """
    The main function of the application.

    Creates a repository of components, bartender and smoker streams,
    and launches them. Handles program interruption.
    """
    # Initialize component store
    comp_store: ComponentStore = ComponentStore()

    # Create and start bartender
    bartender_thread: Bartender = Bartender(comp_store)
    bartender_thread.start()

    # Smokers configuration
    smokers_config: List[Tuple[str, str, threading.Semaphore, Tuple[str, str]]] = [
        ("Tobacco Smoker", "Tobacco", comp_store.pairs['PM'], ("Paper", "Matches")),
        ("Paper Smoker", "Paper", comp_store.pairs['TM'], ("Tobacco", "Matches")),
        ("Matches Smoker", "Matches", comp_store.pairs['TP'], ("Tobacco", "Paper"))
    ]

    # Create and start smokers
    smokers: List[Smoker] = [
        Smoker(name, sem, needs, comp_store)
        for name, owned, sem, needs in smokers_config
    ]

    for smoker in smokers:
        smoker.start()

    try:
        # Infinite wait with periodic checks
        while all(smoker.is_alive() for smoker in smokers) and bartender_thread.is_alive():
            time.sleep(0.5)
    except KeyboardInterrupt:
        print("\nShutting down by user request")
        sys.exit(0)


if __name__ == "__main__":
    main()
