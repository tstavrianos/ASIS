ScriptName randomizedSpawner Extends Actor  
{Sets up the random spawning for ASIS.  Do not use in CK, it's set via SkyProc.}

import Utility

FormList property listOfInvalidCells auto
FormList property listOfNPCs auto
Faction property uniqueSpawnFaction auto
Faction property uniqueSpawnFaction2 auto
bool property isAmbushSpawn auto
float property numReducedSpawns auto
float property numIncreasedSpawns auto
int property numMaxSpawns auto
int property randomSpawnChance auto
int property multiGroupChance auto
int property numMaxGroups auto
FormList property npcsToGetRandomSpawns auto
Form Property SelfForm Auto
Faction[] Property modExcludeFactions Auto

GlobalVariable property ASISRsRandomSpawnChance auto
GlobalVariable property ASISRsMultiGroupChance auto
GlobalVariable property ASISRsUseInteriorSpawns auto
GlobalVariable property ASISRsSpawnWeight0 auto
GlobalVariable property ASISRsSpawnWeight1 auto
GlobalVariable property ASISRsSpawnWeight2 auto
GlobalVariable property ASISRsSpawnWeight3 auto
GlobalVariable property ASISRsSpawnWeight4 auto
GlobalVariable property ASISRsSpawnWeight5 auto
GlobalVariable property ASISRsSpawnWeight6 auto
GlobalVariable property ASISRsSpawnWeight7 auto
GlobalVariable property ASISRsSpawnWeight8 auto
GlobalVariable property ASISRsSpawnWeight9 auto

float property spawn0weight auto
float property spawn1weight auto
float property spawn2weight auto
float property spawn3weight auto
float property spawn4weight auto
float property spawn5weight auto
float property spawn6weight auto
float property spawn7weight auto
float property spawn8weight auto
float property spawn9weight auto

bool creatureSpawned = false ; For prevention of repeated spawns on new OnLoad() Events
bool firstLoad = true ; For deletion of new spawns
float initialDay
float deleteTime
int spawnRoll
bool useInteriorSpawns


bool inFaction = false

Event OnLoad()
	;Debug.OpenUserLog("ASISLog")
	Actor spawn = self as Actor
	
	;Debug.TraceUser("ASISLog", "--Before the wait.." + spawn as String + "<-- This actor--")
	;Debug.TraceUser("ASISLog", "--Before the wait.." + spawn.GetActorBase() as String + "<-- This base object--")
	
	;A wait placed to confirm that any actors get placed with the unique spawn faction if they shouldn't have extra spawns.
	Wait(0.2)
	If (IsInFaction(UniqueSpawnFaction) )
		inFaction = True
	EndIf
	If (npcsToGetRandomSpawns.HasForm(SelfForm))
		
		useInteriorSpawns = (ASISRsUseInteriorSpawns.GetValue() == 0)
		
		If ((useInteriorSpawns) || !IsInInterior() )
			spawnRoll = Utility.RandomInt(0, 100)
			randomSpawnChance = ASISRsRandomSpawnChance.GetValue() As Int
			If spawnRoll <= randomSpawnChance
				getGlobalValues()
				;Debug.TraceUser("ASISLog", "--The factions and check ->" + uniqueSpawnFaction as String)
				If !inFaction && !listOfInvalidCells.HasForm(spawn.GetParentCell()) && !spawn.IsInFaction(uniqueSpawnFaction2) && !ModFactionExclude()
					;Debug.TraceUser("ASISLog", "--made it inside the faction check and hasForm check..--")
					If creatureSpawned == False
						
						;Debug.TraceUser("ASISLog", "--It got to before the variable declaration.")
						Int size = listOfNPCs.GetSize() - 1
						Int listx = Utility.RandomInt(0, size)
						Debug.TraceUser("ASISLog", "--size: " + size + " listx: " + listx)
						Debug.TraceUser("ASISLog", "--Before while loop.")
						;While (listx > listOfNPCs.GetSize())
						;	listx = listx - 1
						;	Debug.TraceUser("ASISLog", "Inside while: " + listx)
						;EndWhile
						;Debug.TraceUser("ASISLog", "--listx after while: " + listx)
						LeveledActor newSpawn = listOfNPCs.GetAt(listx) As LeveledActor
						;Debug.TraceUser("ASISLog", "--NewSpawn: " + newSpawn as String + " and the spawnExtras()")
						spawnExtras(newSpawn)
						;Debug.TraceUser("ASISLog", "These settings, numGroups: " + numMaxGroups)
						;Debug.TraceUser("ASISLog", "These settings, multiGroupChance: " + multiGroupChance)
						;Debug.TraceUser("ASISLog", "These settings, randomSpawnChance: " + randomSpawnChance)
						Int numGroups = 1
						While (numGroups < numMaxGroups)
							If Utility.RandomInt(0, 100) <= multiGroupChance
								listx = Utility.RandomInt(0, size)
								newSpawn = listOfNPCs.GetAt(listx) As LeveledActor
								spawnExtras(newSpawn)
							EndIf
							numGroups += 1
						EndWhile
						creatureSpawned = True
						
					EndIf
				EndIf
				
				If spawn.IsInFaction(uniqueSpawnFaction)
					If firstLoad == True
						initialDay = Utility.GetCurrentGameTime()
						deleteTime = initialDay + (Game.GetGameSettingInt("iHoursToRespawnCellCleared") * 3600)
						firstLoad = False
					ElseIf firstLoad == False && Utility.GetCurrentGameTime() >= deleteTime
						Disable()
						Delete()
					EndIf
				EndIf
			EndIf
		EndIf
	Else
		Debug.Trace("ASIS Random Spawns script removed. Attempting cleanup.")
		If inFaction
			Disable()
			Delete()
		EndIf
	EndIf
	
	Debug.CloseUserLog("ASISLog")
EndEvent

Function getGlobalValues()
	spawn0weight = ASISRsSpawnWeight0.GetValue()
	spawn1weight = ASISRsSpawnWeight1.GetValue()
	spawn2weight = ASISRsSpawnWeight2.GetValue()
	spawn3weight = ASISRsSpawnWeight3.GetValue()
	spawn4weight = ASISRsSpawnWeight4.GetValue()
	spawn5weight = ASISRsSpawnWeight5.GetValue()
	spawn6weight = ASISRsSpawnWeight6.GetValue()
	spawn7weight = ASISRsSpawnWeight7.GetValue()
	spawn8weight = ASISRsSpawnWeight8.GetValue()
	spawn9weight = ASISRsSpawnWeight9.GetValue()
	
	multiGroupChance = ASISRsMultiGroupChance.GetValue() As Int
EndFunction

Function SpawnExtras(LeveledActor lvln)
	;Debug.TraceUser("ASISLog", "before checkReduced Spawns normal")
	checkReducedSpawns()
	checkIncreasedSpawns()
	;Debug.TraceUser("ASISLog", "Pre-Check numSpawns: " + numSpawns as String)
	Int numSpawns = getSpawnNum()
	If numSpawns > numMaxSpawns
		numSpawns = numMaxSpawns
	EndIf
	;Debug.TraceUser("ASISLog", "After-Check numSpawns: " + numSpawns as String)
	If IsDead() == False
		Int currentElement = 0
		While (currentElement < numSpawns)
			int numForms = lvln.GetNumForms()
			int pick = Utility.RandomInt(0, numForms)
			Form toSpawn = lvln.getNthForm(pick)
			Actor temp = PlaceActorAtMe(toSpawn as ActorBase)
			temp.AddToFaction(uniqueSpawnFaction)
			temp.AddToFaction(uniqueSpawnFaction2)
			If isAmbushSpawn == True
				temp.SetAV("Aggression", 2)
				;Debug.TraceUser("ASISLog", "The aggression level is: " + temp.GetAV("Aggression"))
			EndIf
			;Debug.TraceUser("ASISLog", "The first spawned NPC is: " + temp as String)
			currentElement += 1
		EndWhile
	EndIf
EndFunction

Int Function getSpawnNum()
	Float xSize = 0
	Float maxNum = spawn0weight + spawn1weight + spawn2weight + spawn3weight + spawn4weight + spawn5weight + spawn6weight + spawn7weight + spawn8weight + spawn9weight
	spawn1weight = spawn0weight + spawn1weight
	spawn2weight = spawn1weight + spawn2weight
	spawn3weight = spawn2weight + spawn3weight
	spawn4weight = spawn3weight + spawn4weight
	spawn5weight = spawn4weight + spawn5weight
	spawn6weight = spawn5weight + spawn6weight
	spawn7weight = spawn6weight + spawn7weight
	spawn8weight = spawn7weight + spawn8weight
	spawn9weight = spawn8weight + spawn9weight
	xSize = Utility.RandomFloat(1, maxNum)
	If xSize <= spawn0weight
		Return 0
	ElseIf xSize <= spawn1weight
		Return 1
	ElseIf xSize <= spawn2weight
		Return 2
	ElseIf xSize <= spawn3weight
		Return 3
	ElseIf xSize <= spawn4weight
		Return 4
	ElseIf xSize <= spawn5weight
		Return 5
	ElseIf xSize <= spawn6weight
		Return 6
	ElseIf xSize <= spawn7weight
		Return 7
	ElseIf xSize <= spawn8weight
		Return 8
	ElseIf xSize <= spawn9weight
		Return 9
	EndIf
EndFunction

Function checkReducedSpawns()
	If numReducedSpawns != -1 && numReducedSpawns != 0
		spawn1weight = spawn1weight / numReducedSpawns
		spawn2weight = spawn2weight / numReducedSpawns
		spawn3weight = spawn3weight / numReducedSpawns
		spawn4weight = spawn4weight / numReducedSpawns
		spawn5weight = spawn5weight / numReducedSpawns
		spawn6weight = spawn6weight / numReducedSpawns
		spawn7weight = spawn7weight / numReducedSpawns
		spawn8weight = spawn8weight / numReducedSpawns
		spawn9weight = spawn9weight / numReducedSpawns
	EndIf
EndFunction

Function checkIncreasedSpawns()
	If numIncreasedSpawns != -1 && numIncreasedSpawns != 0
		spawn1weight = spawn1weight * numIncreasedSpawns
		spawn2weight = spawn2weight * numIncreasedSpawns
		spawn3weight = spawn3weight * numIncreasedSpawns
		spawn4weight = spawn4weight * numIncreasedSpawns
		spawn5weight = spawn5weight * numIncreasedSpawns
		spawn6weight = spawn6weight * numIncreasedSpawns
		spawn7weight = spawn7weight * numIncreasedSpawns
		spawn8weight = spawn8weight * numIncreasedSpawns
		spawn9weight = spawn9weight * numIncreasedSpawns
	EndIf
EndFunction

bool Function ModFactionExclude()
	int i = 0
	while i < modExcludeFactions.Length
		Faction f = modExcludeFactions[i]
		if IsInFaction(f)
			return True
		EndIf
		i += 1
	EndWhile
	
	return False
EndFunction

Event OnDying(Actor akKiller)
	If IsInFaction(UniqueSpawnFaction)
		DeleteWhenAble()
	EndIf
EndEvent


