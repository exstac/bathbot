#include <ESP8266WiFi.h>
#include <WiFiClient.h>

#include "keys.h"

static const int BR_FREE = 0;
static const int BR_BUSY = 1;
static const int FREE_TO_BUSY = 2;
static const int BUSY_TO_FREE = 3;

static const int BR_BUSY_TO = 10;
static const int BR_BUSY_BLINK = BR_BUSY_TO - 3;

static const int RED = D2;
static const int GREEN = D3;

void setup() {
  Serial.begin(115200);
  delay(500);

  pinMode(D1, INPUT);
  pinMode(D2, OUTPUT);
  pinMode(D3, OUTPUT);
  
  digitalWrite(GREEN, LOW);
  digitalWrite(RED, LOW);

  Serial.print("Connecting to ");
  Serial.print(ssid);
  WiFi.begin(ssid, pwd);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("WiFi Connect Failed! Rebooting...");
    delay(1000);
    ESP.restart();
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  delay(1000);
}

//const char* host = "10.25.12.52";
const char* host = "192.168.1.22";
int secondsBusy = 0;
int msBusy = 0;
int state = BR_FREE;

int updateStatus(int flr, int busy);

void loop() {
  delay(100);
  int sensor = digitalRead(D1);

  Serial.print(state);
  Serial.print(": sensor: ");
  Serial.print(sensor);
  Serial.print("\tbusy: ");
  Serial.print(secondsBusy);
  Serial.print(".");
  Serial.println(msBusy);

  switch (state) {
    case BR_FREE:
      digitalWrite(GREEN, HIGH);
      digitalWrite(RED, LOW);
      if (sensor) state = FREE_TO_BUSY;
      break;
    case FREE_TO_BUSY:
      // If status update fails, return and retry
      if (updateStatus(4, 1)) return;
      state = BR_BUSY;
      break;
    case BR_BUSY:
      digitalWrite(GREEN, LOW);
      digitalWrite(RED, HIGH);
      msBusy += 100;
      if (msBusy >= 1000) {
        secondsBusy += 1;
        msBusy -= 1000;
      }

      // Reset the timer if the sensor detects motion
      if (sensor) secondsBusy = msBusy = 0;
      // Blink the busy light to let occupant know the bathroom will be marked as free soon
      if (secondsBusy >= BR_BUSY_BLINK && msBusy < 500) digitalWrite(RED, LOW);
      // Transition to free when the timeout is reached
      if (secondsBusy >= BR_BUSY_TO) state = BUSY_TO_FREE;
      break;
    case BUSY_TO_FREE:
      // If status update fails, return and retry
      if (updateStatus(4, 0)) return;
      secondsBusy = msBusy = 0;
      state = BR_FREE;
      break;
  }
}

int updateStatus(int flr, int busy) {
  WiFiClient client;
  if (!client.connect(host, 8080)) {
    Serial.println("Connection failed");
    return -1;
  }
  String body = String("floor=") + flr + "&busy=" + busy;
  client.print("POST /bathroom HTTP/1.0\r\n");
  client.print("User-Agent: BathBot/1.0\r\n");
  client.print("Content-Type: application/x-www-form-urlencoded\r\n");
  client.print("Content-Length: ");
  client.print(body.length());
  client.print("\r\n\r\n");
  client.print(body);
  client.print("\r\n\r\n\r\n");
  return 0;
}
