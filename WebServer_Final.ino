#include <SPI.h>
#include <Ethernet.h>
#include "DHT.h"

#define DHTPIN 2 // pin of DHT sensor   
#define DHTTYPE DHT11 // DHT 11

const int sensorMin = 0;     // minimum timi aisthitirwn
const int sensorMax = 1023;  // maximum timi aisthitirwn
int range1,range2;
//int timerunning=0;

// Arxikopoihsh DHT aisthitira
DHT dht(DHTPIN, DHTTYPE);

int ledPin = 5; // Xrisimopoiw to pin 4 gia to led 
int pirPin = 3; // kai to pin 3 gia ton aisthitira kinisis
int pirValue; // Timi tou aisthitira (HIGH - LOW)

// mac address (my arduino's default mac)
byte mac[] = {
  0xA8, 0x61, 0x0A, 0xAE, 0x69, 0x83
};

byte gateway[] = { 192, 168, 2, 1 };// internet access via router
byte subnet[] = { 255, 255, 255, 0 };// subnet mask
IPAddress ip(192, 168, 2, 40);// Arduino's IP Address
EthernetServer server(80);// port

// Setup function runs only once,when arduino powers on.
void setup() {
  // ledPin defined as output. LOW start value
  pinMode(ledPin,OUTPUT);
  digitalWrite(ledPin, LOW);
  // PIR Motion Sensor pin defined as input
  pinMode(pirPin,INPUT);
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
  // start the Ethernet connection and the server:
  Ethernet.begin(mac, ip, gateway, subnet);
  dht.begin();
    // Check for Ethernet hardware present
  if (Ethernet.hardwareStatus() == EthernetNoHardware) {
    Serial.println("Ethernet shield was not found.  Sorry, can't run without hardware. :(");
  }
  if (Ethernet.linkStatus() == LinkOFF) {
    Serial.println("Ethernet cable is not connected.");
  }
  // start server
  server.begin();
  Serial.print("server is at ");
  Serial.println(Ethernet.localIP());
}

void loop() {
  
  // Wait a few seconds between measurements.
  pirValue = digitalRead(pirPin);
  digitalWrite(ledPin, pirValue);
  delay(2000);
  EthernetClient client = server.available();
  if (client) {
    Serial.println("new client");
    // an http request ends with a blank line
    boolean currentLineIsBlank = true;
    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        Serial.write(c);
        // if you've gotten to the end of the line (received a newline
        // character) and the line is blank, the http request has ended,
        // so you can send a reply
        if (c == '\n' && currentLineIsBlank) {
          // send a standard http response header
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println("Connection: close");  // the connection will be closed after completion of the response
          //client.println("Refresh: 5");  // refresh the page automatically every 5 sec
          client.println();
          //client.println(F("<!DOCTYPE HTML>"));
          //client.println(F("<html>"));
          //client.println(F("Temperature:"));
          client.print(dht.readTemperature());
          client.print(" ");
          //client.println(F("Humidity:"));
          client.print(dht.readHumidity());
          client.print(" ");          
          //client.println(F("<br>"));
          client.print(range1 = map(analogRead(A0), sensorMin, sensorMax, 0, 3));
          client.print(" ");
          //client.println(F("<br>"));
          client.print(range2 = map(analogRead(A1), sensorMin, sensorMax, 0, 3));
          client.print(" ");
          client.print(pirValue = digitalRead(pirPin));
          client.print(" ");
          //digitalWrite(ledPin, pirValue);
          //client.println(F("<br>"));
          //timerunning = timerunning + 5;
          //client.println(F("Time up:"));
          //client.println(timerunning);
          //client.println(F(" sec"));
          //client.println(F("</html>"));
          break;
        }
        if (c == '\n') {
          // you're starting a new line
          currentLineIsBlank = true;
        } else if (c != '\r') {
          // you've gotten a character on the current line
          currentLineIsBlank = false;
        }
      }
    }
    // give the web browser time to receive the data
    delay(1);
    // close the connection:
    client.stop();
    Serial.println("client disconnected");
  }
}
