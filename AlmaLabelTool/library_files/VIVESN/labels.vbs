'-Zet in labeltool.ini slechts één lijn multipleSpine:
multipleSpine=spinemulti
'-Zet het bestand labels.txt in bijlage in de map C:\Program Files\BIAF\BiafLabel\Templates en hernoem het naar labels.vbs 
'Je kan een shortcut op de desktop maken naar dit bestand om om te schakelen tussen de formaten.
'-Hernoem C:\Program Files\BIAF\BiafLabel\Templates\spine3x9.lbs naar spinemulti.lbs
'-Kopieer C:\Program Files\BIAF\BiafLabel\Templates\spine3x9.lrs naar spinemulti.lrs
'Telkens je op de shortcut naar labels.vbs klikt wordt spine3x8.lbs of spine3x9.lbs hernoemt naar spinemulti.lbs
'Je gaat waarschijnlijk ook marges/papierinstellingen moeten bijsturen voor spine3x8 en spine3x9 in BIAFLabel. Je gaat altijd maar één van beide bestanden terugvinden vanuit BIAFLabel: de andere is immers hernoemt naar spinemulti!

Option Explicit

DIM fso

Set fso = CreateObject("Scripting.FileSystemObject")

If (fso.FileExists("spine3x8.lbs")) Then
    fso.MoveFile "spinemulti.lbs", "spine3x9.lbs"
    fso.MoveFile "spine3x8.lbs", "spinemulti.lbs"
    WScript.Echo("Klaar om 3x8 labels te drukken")
Else
    fso.MoveFile "spinemulti.lbs", "spine3x8.lbs"
    fso.MoveFile "spine3x9.lbs", "spinemulti.lbs"
    WScript.Echo("Klaar om 3x9 labels te drukken")
End If