# WCT³ GUI

WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate the 
timetabling process at the WIAI faculty of the University of Bamberg. It was 
developed by [Nicolas Gross](https://github.com/nicolasgross) as part of his 
bachelor thesis at the Software Technologies Research Group (SWT).

This is the GUI module of WCT³. Its interface comprises functionality to view 
generated timetables, edit semester data and to generate new timetables.


## Notice

Sometimes the 'Rename' menu item in the context menu of the timetable list stays 
visually disabled despite only one timetable is selected. However, the menu item
still works as expected, it just looks like it is disabled all the time. This 
behaviour is not a bug in WCT³ GUI but in the JDK, the bug report can be found 
[here](https://bugs.openjdk.java.net/browse/JDK-8201310).


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
