__author__ = 'Dan'
import re

weekdaytxt = open("html/weekday.txt",'r').read()
reWeekdayNorth = r'Weekday Northbound.*?</table>'
reWeekdaySouth = r'Weekday Southbound.*?</table>'
reRows = r'<tr.*?</tr>'
reStopNums = r'(?<=>)[0-9]{3}(?=<)'
reStopTimes = r'(?<=<td>)&nbsp; (?=</td>)|(?<=<em>)[0-9]+:[0-9]+(?=</em>)|(?<=<td>)<br />.*?(?=</td>)|(?<=<strong>)[0-9]+:[0-9]+(?=</strong>)'
reStopName = r'(?<=title=").*?(?=">)'
reRowTd = r'<td.*?</td>'
reNbsp = r'&nbsp'
reBr = r'<br />'
reAmTime = r'(?<=<em>)[0-9]+:[0-9]+(?=</em>)'
rePmTime = r'(?<=<strong>)[0-9]+:[0-9]+(?=</strong>)|(?<="style2">)[0-9]+:[0-9]+(?=</span>)'
timetable = []
wdNorthList = []
wdSouthList = []


wdNorth = re.findall(reWeekdayNorth,weekdaytxt,re.DOTALL)[0]
wdNorthRows = re.findall(reRows,wdNorth,re.DOTALL)
wdNorthStopNums = re.findall(reStopNums,wdNorthRows[0],re.DOTALL)

wdSouth = re.findall(reWeekdaySouth,weekdaytxt,re.DOTALL)[0]
wdSouthRows = re.findall(reRows,wdSouth,re.DOTALL)
wdSouthStopNums = re.findall(reStopNums,wdSouthRows[0],re.DOTALL)
wdSouthRows = wdSouthRows[::-1] #invert south schedule

def writeOut(schedule):
    f = open('schedules.txt','w')
    for arr in schedule:
        f.write(','.join(arr)+'\n')
    f.close()

def adjustTime(pm,time):
    hour = re.findall(r'[0-9]+(?=:)',time[0])[0]
    min = re.findall(r'(?<=:)[0-9]+',time[0])[0]
    hour = int(hour) % 12
    if pm:
        hour += 12
    return str(hour) + ":"+ str(min)

def rowTimes(row):
    ret = []
    tds = re.findall(reRowTd,row,re.DOTALL)
    for td in tds:
        amTime = re.findall(reAmTime,td,re.DOTALL)
        pmTime = re.findall(rePmTime,td,re.DOTALL)
        if len(amTime) > 0:
            ret.append(adjustTime(False,amTime))
        elif len(pmTime) > 0:
            ret.append(adjustTime(True,pmTime))
        else:
            ret.append('')
    return ret

rowCount = len(wdNorthRows)
i=0;
while i < rowCount-1:
    if i==0:
        name = "station"
        timetable.append([name] + wdNorthStopNums + wdSouthStopNums)
    else:
        name = re.findall(reStopName,wdNorthRows[i],re.DOTALL)[0]
        timetable.append(([name] + rowTimes(wdNorthRows[i]) + rowTimes(wdSouthRows[i])))
    i+=1

timetable.insert(17,["Atherton"] + ['' for x in range(len(wdNorthStopNums)+len(wdSouthStopNums))])
writeOut(timetable)
print wdNorthStopNums
print wdSouthStopNums