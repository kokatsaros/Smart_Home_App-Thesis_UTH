#include <SPI.h>
#include <Ethernet.h>
#include "DHT.h"

#define DHTPIN 2 // pin of DHT sensor   
#define DHTTYPE DHT11 // DHT 11

const int sensorMin = 0;     // Analog sensor min value
const int sensorMax = 1023;  // Analog sensor max value
int range1,range2;

// initialization DHT sensor
DHT dht(DHTPIN, DHTTYPE);

int ledPin = 5; // Pin 4  used for led 
int pirPin = 3; // Pin 3 for PIR motion sensor
int pirValue; // The value of Motion sensor(HIGH - LOW)

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
  // Check if Ethernet cable is on
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
          client.println("Content-Type: text");
          client.println("Connection: close");  // the connection will be closed after completion of the response
          client.println();
          client.print(dht.readTemperature());
          client.print(" ");
          client.print(dht.readHumidity());
          client.print(" ");          
          client.print(range1 = map(analogRead(A0), sensorMin, sensorMax, 0, 3));
          client.print(" ");
          client.print(range2 = map(analogRead(A1), sensorMin, sensorMax, 0, 3));
          client.print(" ");
          client.print(pirValue = digitalRead(pirPin));
          client.print(" ");
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
