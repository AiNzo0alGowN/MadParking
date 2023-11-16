import json
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
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

def scrape_parking_lot_info(url):
    driver.get(url)
    sleep(2)  # Wait for the page to load

    # Interact with the menu to reveal public parking information
    menu_button = driver.find_element(By.CSS_SELECTOR, 'button.map-mobile-menu-button-bar')
    menu_button.click()
    sleep(2)  # Wait for the menu to open

    # Locate and click the 'Public Parking' checkbox
    public_parking_checkbox = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, "input[name='parking-layer']"))
    )
    if not public_parking_checkbox.is_selected():
        public_parking_checkbox.click()
    sleep(2)  # Wait for the parking information to load

    # Scrape and update parking data
    parking_lots_info = {}
    available_parking_elements = driver.find_elements(By.CSS_SELECTOR, '.available_parking')
    for element in available_parking_elements:
        lot_name = element.find_element(By.CSS_SELECTOR, '.lot_number').text.strip()
        spot_count_elements = element.find_elements(By.CSS_SELECTOR, '.spot-count')
        if spot_count_elements:
            spot_count = spot_count_elements[0].text.strip()
            parking_lots_info[lot_name] = {'availability': spot_count}

    return parking_lots_info

# URL of the parking map
url = "https://map.wisc.edu/"
new_parking_lots_info = scrape_parking_lot_info(url)
driver.quit()

# File path of the existing data
filename = 'garages_full_info.json'

# Read the existing JSON file and update it with new data
try:
    with open(filename, 'r', encoding='utf-8') as file:
        existing_data = json.load(file)
except FileNotFoundError:
    existing_data = {}

# Update the existing data with the new data
existing_data.update(new_parking_lots_info)

# Write the updated data back to the file
with open(filename, 'w', encoding='utf-8') as file:
    json.dump(existing_data, file, ensure_ascii=False, indent=4)

print("Parking data has been updated and saved in 'garages_full_info.json'.")