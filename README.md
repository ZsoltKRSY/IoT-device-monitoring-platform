# DS2025_30641_Korossy_Zsolt_Assignment_1

## Deployment cu docker
Într-un terminal în folderul root al aplicației:
*docker compose up --build* (sau *docker compose up --build -d* pentru a rula in fundal)

Toate dependențele sunt descărcate și construite automat, inclusiv bazele de date, în cazul în care acestea nu există încă (scripturile .sql de inițializare sunt incluse în proiect).

Aplicația Device Data Dimulator (aplicație independentă, nu face parte din deploy) trebuie rulată manual **după fiecare alt serviciu a fost deploy-at cu succes (în special serviciul Devices și brokerul de mesaje)**.
- Dacă doriți să ruleze o singură instanță a aplicației, puteți face acest lucru din IntelliJ (portul implicit al serverului este 8090).
- Dacă doriți mai multe instanțe, trebuie să construiți fișierul JAR al aplicației. În folderul rădăcină, rulați comanda *./mvnw clean package -DskipTests*. După aceasta, rulați comanda *java -jar target/device_data_simulator-0.0.1-SNAPSHOT.jar --server.port=8090* de câte ori doriți în terminale separate din folderul rădăcină al aplicației (**important**, utilizați **porturi de server diferite** pentru fiecare)

Aplicația preia și actualizează automat lista de ID-uri ale dispozitivelor la fiecare 5 minute și generează valori simulate pentru *10 dispozitive selectate aleatoriu*. Pentru a genera date despre dispozitive, se poate urma instrucțiunile consolei (mai întâi trebuie introdus numărul de valori generate, apoi numărul de secunde de așteptare între fiecare valoare generată).

## Accesul la aplicație
- portul 4200: aplicația Angular (frontend) prin care utilizatorul interacționează practic cu aplicația
- portul 15672: dashboard-ul RabbitMQ (folosit pentru monitorizarea cozilor)
- portul 8080: gateway-ul API, lăsat expus pentru a putea fi testat de pe gazdă (de exemplu, cu curl), endpoint-uri protejate cu autentificare bazată pe jwt
- portul 8081: devices service
- portul 8082: users service
- portul 8083: authentication service
- portul 8084: monitoring service

Ultimele 4 sunt expuse doar din cauza Swagger-ului, altfel nu ar trebui să fie, deoarece sunt accesate în rețeaua internă Docker prin intermediul gateway-ului API (iar endpoint-urile nu sunt protejate).

- http://localhost:4200
- http://localhost:15672
- http://localhost:8081/swagger-ui/index.html
- http://localhost:8082/swagger-ui/index.html
- http://localhost:8083/swagger-ui/index.html
- http://localhost:8084/swagger-ui/index.html