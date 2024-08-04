
# Raspberry Monitor

This project is an Android application that monitors and displays system information from a Raspberry Pi, including database records, system metrics like CPU temperature, memory usage, disk usage, power status, and movement logs from a connected Arduino setup. The backend is built using Flask and communicates with the Android app via a REST API.

## Features

- **User Login**: Authenticates users and provides a JWT token for secure access.
- **System Information**: Fetches CPU temperature, memory usage, disk usage, and power status.
- **Database Records**: Retrieves and displays records from a MariaDB database in a tabular format.
- **Graphical Visualization**: Displays system metrics using graphs for better visualization.
- **Movement Logs**: Fetches and displays logs of movements detected by an Arduino-based setup.
- **Weather Radar**: Displays weather information including wind, temperature, pressure, and radar overlays using OpenWeather API and Windy.com.
- **Network Information**: Monitors network traffic and displays total bytes sent and received, along with per-interface statistics.

## Technology Stack

- **Backend**: Flask, Flask-JWT-Extended, Flask-CORS, pymysql, psutil
- **Frontend**: Android (Java), Material Design Components, RecyclerView for displaying data in tables, WebView for charts
- **Database**: MariaDB
- **Weather Visualization**: Windy API, Leaflet.js

## Getting Started

### Prerequisites

- Python 3.7+
- Android Studio
- MariaDB

### Backend Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/raspberry-monitor.git
   cd raspberry-monitor
   ```

2. **Create a Virtual Environment**

   ```bash
   python3 -m venv venv
   source venv/bin/activate   # On Windows: venv\Scripts\activate
   ```

3. **Install Dependencies**

   ```bash
   pip install -r requirements.txt
   ```

4. **Configure the Database**

   Update the `db_configs` dictionary in the `monitor.py` file with your MariaDB credentials.

5. **Run the Backend Server**

   ```bash
   python monitor.py
   ```

### Android Setup

1. **Open the Project in Android Studio**

   Open the `RaspberryMonitor` project in Android Studio.

2. **Build and Run the App**

   Connect an Android device or start an emulator and run the app.

### API Endpoints

- **POST /login**: Authenticates the user and returns a JWT token.
- **GET /api/db_records**: Returns the database records. Requires JWT.
- **GET /api/system_info**: Returns system information. Requires JWT.
- **POST /api/movements**: Records a new movement log. Requires JWT.
- **GET /api/get_movements**: Returns movement logs detected by the Arduino setup. Requires JWT.
- **GET /api/network**: Returns network information including total bytes sent and received, and per-interface statistics. Requires JWT.
- **GET /weather**: Serves the weather radar HTML page.

### Weather Radar

The weather radar feature provides real-time weather information including wind speed, temperature, pressure, and radar overlay. It uses the OpenWeather API to fetch weather data based on the user's geolocation.

#### Weather Radar Setup

1. **Replace OpenWeather API Key**

   In the `weather.html` file, replace the placeholder in `const openWeatherApiKey = '';` with your OpenWeather API key.

2. **Serve Static Files**

   Ensure that the `static` directory contains the necessary files (`styles.css`, `scripts.js`, `leaflet.js`, `leaflet.css`).

3. **Access the Weather Radar**

   Navigate to `http://your-server-ip:5000/weather` to view the weather radar.
   The weather radar is also accessible at: [https://ismacarbo.zapto.org/weather](https://ismacarbo.zapto.org/weather).

## Usage

1. **Login**: Enter the username and password to authenticate and receive a token.
2. **Fetch Data**: Select the database from the dropdown and click the "Fetch Data" button to retrieve and display the database records in a table format.
3. **View System Metrics**: System metrics like CPU temperature, memory usage, and disk usage are displayed using graphs for easy visualization.
4. **View Movement Logs**: Access the "Movements" section to view logs of movements detected by the Arduino-based setup.
5. **Weather Radar**: View real-time weather information on the weather radar page.
6. **Network Information**: View network traffic details including total bytes sent and received, and per-interface statistics.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
