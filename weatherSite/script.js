const openWeatherApiKey = ''; // Replace with your OpenWeather API key

let options = {
    key: '', // Replace with your OpenWeather API key
    verbose: true,
    overlay: 'wind', // Default overlay
    zoom: 5,
};

function changeOverlay(newOverlay) {
    options.overlay = newOverlay;
    initWindy();
    if (newOverlay === 'wind') {
        document.getElementById('windSpeedContainer').style.display = 'flex';
        document.getElementById('temperatureContainer').style.display = 'none';
        document.getElementById('pressureContainer').style.display = 'none';
        updateWindSpeed();
    } else if (newOverlay === 'temp') {
        document.getElementById('windSpeedContainer').style.display = 'none';
        document.getElementById('temperatureContainer').style.display = 'flex';
        document.getElementById('pressureContainer').style.display = 'none';
        updateTemperature();
    } else if (newOverlay === 'pressure') {
        document.getElementById('windSpeedContainer').style.display = 'none';
        document.getElementById('temperatureContainer').style.display = 'none';
        document.getElementById('pressureContainer').style.display = 'flex';
        updatePressure();
    } else if (newOverlay === 'radar') {
        document.getElementById('windSpeedContainer').style.display = 'none';
        document.getElementById('temperatureContainer').style.display = 'none';
        document.getElementById('pressureContainer').style.display = 'none';
        document.getElementById('radarImage').style.display = 'block';
    } else {
        document.getElementById('windSpeedContainer').style.display = 'none';
        document.getElementById('temperatureContainer').style.display = 'none';
        document.getElementById('pressureContainer').style.display = 'none';
    }
}

function updateWindSpeedUnit() {
    const unitSelect = document.getElementById('unitSelect').value;
    const windSpeedValue = parseFloat(document.getElementById('windSpeedValue').textContent);
    let newValue;

    switch (unitSelect) {
        case 'km/h':
            newValue = (windSpeedValue * 3.6).toFixed(2);
            break;
        case 'mph':
            newValue = (windSpeedValue * 2.23694).toFixed(2);
            break;
        case 'm/s':
            newValue = windSpeedValue.toFixed(2);
            break;
        default:
            newValue = windSpeedValue.toFixed(2);
    }

    document.getElementById('windSpeedValue').textContent = newValue;
    document.getElementById('windSpeedUnit').textContent = unitSelect;
}

function updateTemperatureUnit() {
    const unitSelect = document.getElementById('tempUnitSelect').value;
    const temperatureValue = parseFloat(document.getElementById('temperatureValue').textContent);
    let newValue;

    if (unitSelect === 'F') {
        newValue = (temperatureValue * 9/5 + 32).toFixed(2);
    } else {
        newValue = ((temperatureValue - 32) * 5/9).toFixed(2);
    }

    document.getElementById('temperatureValue').textContent = newValue;
    document.getElementById('temperatureUnit').textContent = `°${unitSelect}`;
}

function updatePressureUnit() {
    const unitSelect = document.getElementById('pressureUnitSelect').value;
    const pressureValue = parseFloat(document.getElementById('pressureValue').textContent);
    let newValue;

    if (unitSelect === 'inHg') {
        newValue = (pressureValue * 0.02953).toFixed(2);
    } else {
        newValue = (pressureValue / 0.02953).toFixed(2);
    }

    document.getElementById('pressureValue').textContent = newValue;
    document.getElementById('pressureUnit').textContent = unitSelect;
}

function updateWindSpeed() {
    navigator.geolocation.getCurrentPosition(position => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        fetch(`https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${openWeatherApiKey}&units=metric`)
            .then(response => response.json())
            .then(data => {
                const windSpeed = data.wind.speed; // Wind speed in m/s
                document.getElementById('windSpeedValue').textContent = windSpeed.toFixed(2); // Display in m/s by default
                updateWindSpeedUnit();
            })
            .catch(error => console.error('Error fetching wind data:', error));
    }, error => {
        console.error('Error getting geolocation: ', error);
    });
}

function updateTemperature() {
    navigator.geolocation.getCurrentPosition(position => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        fetch(`https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${openWeatherApiKey}&units=metric`)
            .then(response => response.json())
            .then(data => {
                const temperature = data.main.temp; // Temperature in Celsius
                document.getElementById('temperatureValue').textContent = temperature.toFixed(2); // Display in °C by default
                updateTemperatureUnit();
            })
            .catch(error => console.error('Error fetching temperature data:', error));
    }, error => {
        console.error('Error getting geolocation: ', error);
    });
}

function updatePressure() {
    navigator.geolocation.getCurrentPosition(position => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        fetch(`https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${openWeatherApiKey}&units=metric`)
            .then(response => response.json())
            .then(data => {
                const pressure = data.main.pressure; // Pressure in hPa
                document.getElementById('pressureValue').textContent = pressure.toFixed(2); // Display in hPa by default
                updatePressureUnit();
            })
            .catch(error => console.error('Error fetching pressure data:', error));
    }, error => {
        console.error('Error getting geolocation: ', error);
    });
}

function initWindy() {
    windyInit(options, windyAPI => {
        const { map } = windyAPI;
        // You can access the map object here and customize it if needed
    });
}

// Initialize with default overlay
initWindy();
