import json
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.chrome.options import Options
from time import sleep

# Set Chrome options
chrome_options = Options()
chrome_options.add_argument("--headless")  # Headless mode
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")

# Initialize WebDriver
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=chrome_options)

def scrape_garage_info(url):
    driver.get(url)
    sleep(1)  # Wait for the page to load
    garage_info = {'url': url}

    # Get garage name
    try:
        garage_name = driver.find_element(By.CSS_SELECTOR, 'h1.page-title').text
        garage_info['name'] = garage_name
    except Exception as e:
        # garage_info['name_error'] = str(e)
        print(f"Error fetching name for garage at {url}: {e}")

    # Get the number of available parking spots
    try:
        availability = driver.find_element(By.CSS_SELECTOR, 'h2.alert-title + p').text
        garage_info['availability'] = availability
    except Exception as e:
        # garage_info['availability_error'] = str(e)
        garage_info['availability'] = "unknown"
        print(f"Error fetching availability for {garage_info['name']}")

    # Get address information
    try:
        address_elements = driver.find_elements(By.CSS_SELECTOR, 'address')
        addresses = [address.text.rstrip('Directions') for address in address_elements]
        garage_info['addresses'] = addresses
    except Exception as e:
        # garage_info['address_error'] = str(e)
        print(f"Error fetching addresses for {garage_info['name']}")

    # Get electric vehicle charging information
    try:
        ev_charging = driver.find_element(By.XPATH, "//h2[contains(text(), 'Electric Vehicles')]/following-sibling::div").text
        garage_info['ev_charging'] = ev_charging
    except Exception as e:
        # garage_info['ev_charging_error'] = str(e)
        print(f"Error fetching ev_charging for {garage_info['name']}")

    # Get garage clearance
    try:
        clearance = driver.find_element(By.XPATH, "//h2[contains(text(), 'Clearance')]/following-sibling::div").text
        garage_info['clearance'] = clearance
    except Exception as e:
        # garage_info['clearance_error'] = str(e)
        print(f"Error fetching clearance for {garage_info['name']}")

    # Get operating hours
    try:
        hours = driver.find_element(By.XPATH, "//h2[contains(text(), 'Hours of Operation')]/following-sibling::div").text
        garage_info['hours'] = hours
    except Exception as e:
        # garage_info['hours_error'] = str(e)
        print(f"Error fetching hours for {garage_info['name']}")

    # Get nearby locations
    try:
        locations_nearby = driver.find_element(By.XPATH, "//h2[contains(text(), 'Locations Nearby')]/following-sibling::div").text
        garage_info['locations_nearby'] = locations_nearby
    except Exception as e:
        # garage_info['locations_nearby_error'] = str(e)
        print(f"Error fetching locations_nearby for {garage_info['name']}")

    # Get parking rates
    try:
        rates = driver.find_element(By.XPATH, "//caption[contains(text(), 'Hourly Rates')]/following-sibling::tbody").text
        garage_info['rates'] = rates
    except Exception as e:
        # garage_info['rates_error'] = str(e)
        print(f"Error fetching rates for {garage_info['name']}")

    return garage_info

# Scrape information for each parking garage
garages_info = {}
garage_availability = {}
urls = [
    "https://www.cityofmadison.com/parking/garages-lots/blair-lot",
    "https://www.cityofmadison.com/parking/garages-lots/buckeye-lot",
    "https://www.cityofmadison.com/parking/garages-lots/capitol-square-north-garage",
    "https://www.cityofmadison.com/parking/garages-lots/evergreen-lot",
    "https://www.cityofmadison.com/parking/garages-lots/overture-center-garage",
    "https://www.cityofmadison.com/parking/garages-lots/south-livingston-street-garage",
    "https://www.cityofmadison.com/parking/garages-lots/state-street-campus-garage",
    "https://www.cityofmadison.com/parking/garages-lots/state-street-capitol-garage",
    "https://www.cityofmadison.com/parking/garages-lots/wilson-lot",
    "https://www.cityofmadison.com/parking/garages-lots/wilson-street-garage",
    "https://www.cityofmadison.com/parking/garages-lots/wingra-lot"
]

for url in urls:
    print(f"Scraping: {url}")
    info = scrape_garage_info(url)
    garage_name = info.get('name', 'Unknown Garage')
    garages_info[garage_name] = info
    if 'availability' in info:
        garage_availability[garage_name] = info['availability']

driver.quit()

with open('garages_full_info.json', 'w', encoding='utf-8') as file:
    json.dump(garages_info, file, ensure_ascii=False, indent=4)
    
print("All data has been saved to 'garages_full_info.json'.")
