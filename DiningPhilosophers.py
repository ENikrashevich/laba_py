import threading
import time

class Philosopher(threading.Thread):
    def __init__(self, index, forks):
        super().__init__()
        self.index = index
        self.forks = forks
        self.left = index
        self.right = (index + 1) % len(forks)

    def run(self):
        for _ in range(5):  # Философ выполняет 5 циклов
            self.think()
            self.eat()

    def think(self):
        print(f"Философ {self.index} размышляет")
        time.sleep(1)  # Имитация времени на размышления

    def eat(self):
        # Определяем порядок взятия вилок
        first, second = sorted([self.left, self.right])
        
        # Берём первую вилку
        self.forks[first].acquire()
        print(f"Философ {self.index} взял вилку {first}")
        
        # Берём вторую вилку
        self.forks[second].acquire()
        print(f"Философ {self.index} взял вилку {second}")
        
        # Процесс еды
        print(f"Философ {self.index} ест")
        time.sleep(1)  # Имитация времени на еду
        
        # Освобождаем вилки в обратном порядке
        self.forks[second].release()
        print(f"Философ {self.index} положил вилку {second}")
        self.forks[first].release()
        print(f"Философ {self.index} положил вилку {first}")

if __name__ == "__main__":
    num_philosophers = 5
    forks = [threading.Lock() for _ in range(num_philosophers)]
    philosophers = [Philosopher(i, forks) for i in range(num_philosophers)]

    for p in philosophers:
        p.start()

    for p in philosophers:
        p.join()