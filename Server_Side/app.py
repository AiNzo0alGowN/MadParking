import json
from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/parking-lots', methods=['GET'])
def get_parking_lots():
    # Read the data file
    with open('garages_full_info.json', 'r', encoding='utf-8') as file:
        data = json.load(file)
    return jsonify({"parkingLots": data})

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
