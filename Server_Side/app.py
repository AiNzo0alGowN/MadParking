import json
from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/parking-lots', methods=['GET'])
def get_parking_lots():
    # Read the data file
    with open('parking_lots.json', 'r') as file:
        data = json.load(file)
    return jsonify(data)

@app.route('/parking-lots/<int:id>', methods=['GET'])
def get_parking_lot(id):
    # Read the data file
    with open('parking_lots.json', 'r') as file:
        data = json.load(file)
    # Find a specific parking lot
    parking_lot = next((item for item in data['parkingLots'] if item['id'] == id), None)
    if parking_lot is not None:
        return jsonify(parking_lot)
    else:
        return jsonify({'message': 'Parking lot not found'}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
