# WCT³ GUI

WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate the 
timetabling process at the WIAI faculty of the University of Bamberg. It was 
developed by [Nicolas Gross](https://github.com/nicolasgross) as part of his 
bachelor thesis at the Software Technologies Research Group (SWT).

This is the GUI module of WCT³. Its interface comprises functionality to view 
generated timetables, edit semester data and to generate new timetables.


## Dependencies

- Oracle JDK 10
- maven
- libwcttt


## Build

1. Install libwcttt in the local maven repository
2. `cd [PATH_TO_PROJECT_ROOT]`
3. `mvn clean package` 


## Run

`java -jar [PATH_TO_PROJECT_ROOT]/target/wcttt-gui-[VERSION].jar`


## License

This software is released under the terms of the GPLv3.
