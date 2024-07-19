from flask import Flask, jsonify, request
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
from flask_cors import CORS
import pymysql
import psutil
import subprocess
from datetime import datetime

app = Flask(__name__)
app.config['JWT_SECRET_KEY'] = 'Your_Secure_Secret_Key'  # Change this to a secure secret key
jwt = JWTManager(app)
CORS(app)

# MariaDB connection configuration
db_configs = {
    'ripetizioni': {
        'user': 'root',
        'password': 'your_db_password',
        'host': 'localhost',
        'database': 'ripetizioni',
    },
    'altro_db': {
        'user': 'root',
        'password': 'your_db_password',
        'host': 'localhost',
        'database': 'altro_db',
    }
}

# Function to verify credentials (improve for production environment)
def verify_user(username, password):
    return username == 'admin' and password == 'admin'  # Example credentials, change for production

@app.route('/login', methods=['POST'])
def login():
    print("Login endpoint hit")
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

@app.route('/logout', methods=['POST'])
@jwt_required()
def logout():
    print("Logout endpoint hit")
    return jsonify({"msg": "Successfully logged out"}), 200

@app.route('/api/db_records', methods=['GET'])
@jwt_required()
def db_records():
    app.logger.debug('Received request for /api/db_records with JWT identity: %s', get_jwt_identity())
    db_name = request.args.get('db', 'ripetizioni')
    if db_name not in db_configs:
        return jsonify({"msg": "Invalid database name"}), 400
    records = get_db_records(db_configs[db_name])
    return jsonify(records)

@app.route('/api/system_info', methods=['GET'])
@jwt_required()
def system_info():
    info = get_system_info()
    return jsonify(info)

@app.route('/api/movements', methods=['GET'])
@jwt_required()
def record_movement():
    app.logger.debug('Received movement data with JWT identity: %s', get_jwt_identity())
    
    movement = request.args.get('movement')
    if not movement:
        return jsonify({"msg": "Missing movement data"}), 400

    # Create a response
    response = {
        'timestamp': datetime.now().isoformat(),
        'movement': movement
    }

    return jsonify(response), 200

def get_db_records(db_config):
    conn = pymysql.connect(**db_config)
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM utenti")
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
    
    power_info = interpret_throttled(power)
    return {
        'temperature': temp['cpu_thermal'][0].current if 'cpu_thermal' in temp else None,
        'memory': memory.percent,
        'disk': disk.percent,
        'power': power_info
    }

def interpret_throttled(value):
    throttled_flags = {
        '0': 'No issues',
        '1': 'Under-voltage',
        '2': 'Arm frequency capped',
        '4': 'Currently throttled',
        '10000': 'Under-voltage has occurred',
        '20000': 'Arm frequency capped has occurred',
        '40000': 'Throttling has occurred'
    }
    explanations = []
    try:
        val = int(value.split('=')[1], 16)
        for flag, explanation in throttled_flags.items():
            if val & int(flag, 16):
                explanations.append(explanation)
        if not explanations:
            explanations.append('No issues')
    except Exception as e:
        explanations.append(f'Error interpreting value: {e}')
    
    return ', '.join(explanations)

if __name__ == '__main__':
    print("Starting Flask server...")
    app.run(host='0.0.0.0', port=5000)
