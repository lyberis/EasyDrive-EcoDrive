# EasyDrive-EcoDrive
Android OBD-II application for encouraging economical driving

This project is the tool used for the conduction of field studies, performed for my Master Thesis. 

It involves the development of an Android application that encourages eco – driving.

The application, through the usage of OBD-II diagnostic protocol and Bluetooth communication with ELM327 device, collects, displays, processes and stores, in real time, data concerning vehicle speed, RPM and Throttle Position.
The application development was based in the project petrolr_OBDTerminal (https://github.com/M-Helm/petrolr_OBDTerminal), in which we performed all the desired changes and amendments, in order to implement the required functionality.

The application functions in two modes. Firstly, and for parametric predefined number and distance of routes, it functions “silently”, just recording the driver’s driving behavior (Calibration mode). Subsequently and when the calibration is completed, through audible and visible notifications, the system informs the driver about his deviation from the modulated averages of the monitored data. Moreover, when a route is completed the application scores and ranks the driver into levels, based on his performance.

Eclipse IDE was used for the application development and SQLite database for data recording of each route.

Contact me, if you have questions: easydriveecodrive@gmail.com
