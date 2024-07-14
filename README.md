
# Raspberry Monitor

This project is an Android application that monitors and displays system information from a Raspberry Pi, including database records and system metrics like CPU temperature, memory usage, disk usage, and power status. The backend is built using Flask and communicates with the Android app via a REST API.

## Features

- **User Login**: Authenticates users and provides a JWT token for secure access.
- **System Information**: Fetches CPU temperature, memory usage, disk usage, and power status.
- **Database Records**: Retrieves and displays records from a MariaDB database.

## Technology Stack

- **Backend**: Flask, Flask-JWT-Extended, Flask-CORS, pymysql, psutil
- **Frontend**: Android (Java), Material Design Components
- **Database**: MariaDB

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

   Update the `db_config` dictionary in the `app.py` file with your MariaDB credentials.

5. **Run the Backend Server**

   ```bash
   python app.py
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

## Usage

1. **Login**: Enter the username and password to authenticate and receive a token.
2. **Fetch Data**: Click the "Fetch Data" button to retrieve and display the database records and system information.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
