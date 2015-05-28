cp -r . /Users/mvanhorn/AndroidStudioProjects/casino_system/table.progressive.server/src/main/assets/foxy-html/
if [ "$(whoami)" = 'mvanhorn' ]; then cp -r . /Users/mvanhorn/AndroidStudioProjects/casino_system/table.progressive.server/src/main/assets/foxy-html/; fi
adb push index.html /data/foxhorn/html/index.html
adb push scripts /data/foxhorn/html/scripts
adb push css /data/foxhorn/html/css
adb push libs /data/foxhorn/html/libs
adb push view /data/foxhorn/html/view
adb push pages /data/foxhorn/html/pages
adb push img /data/foxhorn/html/img
