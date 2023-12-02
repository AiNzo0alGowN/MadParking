FROM ubuntu:23.10

# Install Python, pip, and other necessary packages
RUN apt-get update && apt-get install -y python3 python3-pip wget gnupg2 unzip cron \
    && rm -rf /var/lib/apt/lists/*

# Install Flask, Selenium, and WebDriver Manager
RUN pip3 install flask selenium webdriver-manager --break-system-packages

# Install Google Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install ChromeDriver
RUN CHROMEDRIVER_VERSION=$(wget -qO- chromedriver.storage.googleapis.com/LATEST_RELEASE) \
    && wget https://chromedriver.storage.googleapis.com/$CHROMEDRIVER_VERSION/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip \
    && mv chromedriver /usr/bin/chromedriver \
    && chmod +x /usr/bin/chromedriver

WORKDIR /app
COPY . /app

# Setup cron jobs
COPY cronfile /etc/cron.d/cronfile
RUN chmod 0644 /etc/cron.d/cronfile
RUN crontab /etc/cron.d/cronfile
RUN touch /var/log/cron.log

# Start cron in the background and run app.py
CMD cron && python3 MadisonGarageInfoScraper.py && python3 CampusGarageInfoScraper.py && python3 ./app.py | tee output.log
