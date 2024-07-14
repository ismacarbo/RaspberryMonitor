
from flask import Flask, jsonify, request
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
from flask_cors import CORS
import pymysql
import psutil
import subprocess

app = Flask(__name__)
app.config['JWT_SECRET_KEY'] = 'your_secret_key'  # Change this to a secure secret key
jwt = JWTManager(app)
CORS(app)

# MariaDB connection configuration
db_config = {
    'user': 'your_username',             # Replace with your username
    'password': 'your_password',         # Replace with your password
    'host': 'localhost',
    'database': 'your_database',
}

# Function to verify credentials (improve for production environment)
def verify_user(username, password):
    return username == 'admin' and password == 'password'  # Example credentials, change for production

@app.route('/login', methods=['POST'])
def login():
    if not request.is_json:
        return jsonify({"msg": "Missing JSON in request"}), 400

    username = request.json.get('username', None)
    password = request.json.get('password', None)

    app.logger.debug('Received login request: username=%s, password=%s', username, password)
    
    if not username or not password:
        return jsonify({"msg": "Missing username or password"}), 400
    
    if not verify_user(username, password):
        return jsonify({"msg": "Bad username or password"}), 401
    
    access_token = create_access_token(identity=username)
    return jsonify(access_token=access_token), 200

# Protected routes with @jwt_required()
@app.route('/api/db_records', methods=['GET'])
@jwt_required()
def db_records():
    app.logger.debug('Received request for /api/db_records with JWT identity: %s', get_jwt_identity())
    records = get_db_records()
    return jsonify(records)

@app.route('/api/system_info', methods=['GET'])
@jwt_required()
def system_info():
    info = get_system_info()
    return jsonify(info)

def get_db_records():
    conn = pymysql.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM table") #insert your query
    rows = cursor.fetchall()
    columns = [desc[0] for desc in cursor.description]
    records = [dict(zip(columns, row)) for row in rows]
    cursor.close()
    conn.close()
    return {"records": records}

def get_system_info():
    temp = psutil.sensors_temperatures()
    memory = psutil.virtual_memory()
    disk = psutil.disk_usage('/')
    try:
        power = subprocess.run(['vcgencmd', 'get_throttled'], capture_output=True).stdout.decode().strip()
    except FileNotFoundError:
        power = "N/A"
    return {
        'temperature': temp['cpu_thermal'][0].current if 'cpu_thermal' in temp else None,
        'memory': memory.percent,
        'disk': disk.percent,
        'power': power
    }

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
