import threading
import time
import random

MAX_WAITING = 5  # Максимальное количество мест в очереди
customers_sem = threading.Semaphore(0)  # Семафор для ожидающих клиентов
barber_sem = threading.Semaphore(0)     # Семафор для готовности парикмахера
finished_sem = threading.Semaphore(0)   # Семафор для завершения стрижки
mutex = threading.Lock()                # Мьютекс для доступа к очереди
waiting_room = MAX_WAITING              # Свободные места в очереди


def barber():
    while True:
        print("Парикмахер спит...")
        customers_sem.acquire()  # Ожидание клиента
        
        with mutex:
            global waiting_room
            waiting_room += 1    # Освобождаем место в очереди
            barber_sem.release()  # Сигнал клиенту о начале стрижки
        
        print("Парикмахер начал стрижку")
        time.sleep(random.uniform(1, 3))  # Время стрижки
        print("Парикмахер закончил стрижку")
        finished_sem.release()   # Сигнал клиенту о завершении


def customer(customer_id):
    global waiting_room
    
    with mutex:
        if waiting_room > 0:
            waiting_room -= 1
            print(f"Клиент {customer_id} сел в очередь. Свободных мест: {waiting_room}")
            customers_sem.release()  # Уведомление парикмахера
        else:
            print(f"Клиент {customer_id} ушел, нет мест.")
            return
    
    barber_sem.acquire()       # Ожидание готовности парикмахера
    print(f"Клиент {customer_id} начал стрижку")
    finished_sem.acquire()     # Ожидание завершения стрижки
    print(f"Клиент {customer_id} покидает парикмахерскую")


if __name__ == "__main__":
    # Запуск потока парикмахера
    barber_thread = threading.Thread(target=barber)
    barber_thread.daemon = True
    barber_thread.start()

    # Генерация клиентов
    customer_id = 1
    while True:
        time.sleep(random.uniform(0.5, 2))  # Случайный интервал между клиентам
        threading.Thread(target=customer, args=(customer_id,)).start()
        customer_id += 1
