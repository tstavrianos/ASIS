ScriptName increasedSpawns Extends Actor 
{Increased Spawns: Takes a Property for the creature
	to spawn, the number of max spawns, and a currently
	useless number of spawns (leave at 0).}

Import Utility

FormList Property listOfValidNPCs Auto

FormList Property listOfValidCells Auto

Faction Property uniqueSpawnFaction Auto

Bool Property isAmbushSpawn Auto

Faction[] Property modExcludeFactions Auto

GlobalVariable Property ASISMaxNumSpawns Auto
GlobalVariable Property ASISSpawnWeight0 Auto
GlobalVariable Property ASISSpawnWeight1 Auto
GlobalVariable Property ASISSpawnWeight2 Auto
GlobalVariable Property ASISSpawnWeight3 Auto
GlobalVariable Property ASISSpawnWeight4 Auto
GlobalVariable Property ASISSpawnWeight5 Auto
GlobalVariable Property ASISSpawnWeight6 Auto
GlobalVariable Property ASISSpawnWeight7 Auto
GlobalVariable Property ASISSpawnWeight8 Auto
GlobalVariable Property ASISSpawnWeight9 Auto
GlobalVariable Property ASISUseInteriorSpawns Auto
GlobalVariable Property ASISMaxNum Auto
GlobalVariable Property ASISIntSpawnsReduceFactor Auto
GlobalVariable Property ASISSpawnMult Auto

Float spawn0weight
Float spawn1weight
Float spawn2weight
Float spawn3weight
Float spawn4weight
Float spawn5weight
Float spawn6weight
Float spawn7weight
Float spawn8weight
Float spawn9weight

Float numMaxSpawns
Float interiorSpawns
Float multiplier

Bool creatureSpawned = False ; For prevention of repeated spawns on new OnLoad() Events
Bool firstLoad = True ; For deletion of new spawns
Float initialDay
Float deleteTime

Form Property SelfForm Auto
Faction internalUniqueSpawnFaction

Bool inFaction = False

Event OnLoad()
	internalUniqueSpawnFaction = UniqueSpawnFaction
	
	Debug.OpenUserLog("ASISLog")
	Actor spawn = Self As Actor
	
	;Debug.TraceUser("ASISLog", "--OnLoad" + SelfAsActor + "<-- This SelfAsActor--" + SelfAsActor.GetFormID() + "<-- This SelfAsActor.GetFormID--" )
	;Debug.TraceUser("ASISLog", "--OnLoad" + internalForm + "<-- This internalForm--" + internalForm.GetFormID() + "<-- This internalForm.GetFormID--" )
	;Debug.TraceUser("ASISLog", "--Before the wait.." + spawn.GetActorBase() as String + "<-- This base object--")
	
	;A wait placed to confirm that any actors get placed with the unique spawn faction if they shouldn't have extra spawns.
	Wait(0.2)
	If (spawn.IsInFaction(UniqueSpawnFaction) )
		inFaction = True
	EndIf
	If (listOfValidNPCs.HasForm(SelfForm))
		;Debug.TraceUser("ASISLog", "--The factions and check ->" + uniqueSpawnFaction as String + "  And this " + listOfValidNPCs.HasForm(spawn) as String)
		If !spawn.IsInFaction(UniqueSpawnFaction) && listOfValidNPCs.HasForm(spawn.GetActorBase()) && !listOfValidCells.HasForm(spawn.GetParentCell()) && !ModFactionExclude()
			;Debug.TraceUser("ASISLog", "--made it inside the faction check and hasForm check..--")
			If creatureSpawned == False
				;Debug.TraceUser("ASISLog", "--It got to before the spawnExtras function...--")
				spawnExtras(spawn, spawn.GetActorBase())
				creatureSpawned = True
			EndIf
		EndIf
		
		
		
		If spawn.IsInFaction(internalUniqueSpawnFaction)
			If firstLoad == True
				initialDay = Utility.GetCurrentGameTime()
				deleteTime = initialDay + (Game.GetGameSettingInt("iHoursToRespawnCellCleared") / 24)
				firstLoad = False
			ElseIf firstLoad == False && Utility.GetCurrentGameTime() >= deleteTime
				Disable()
				Delete()
			EndIf
		EndIf
	Else
		Debug.Trace("ASIS IncreasedSpawns script removed. Attempting cleanup.")
		If inFaction
			Disable()
			Delete()
		EndIf
	EndIf
	
	;Debug.CloseUserLog("ASISLog")
EndEvent

Function SpawnExtras(Actor arSpawnRef, ActorBase newSpawn)
	
	;Debug.TraceUser("ASISLog", "Pre-Check numSpawns: " + numSpawns as String)
	Int numSpawns = getSpawnNum() As Int
	numMaxSpawns = (ASISMaxNumSpawns.GetValue() As Int)
	If numSpawns > numMaxSpawns
		numSpawns = (numMaxSpawns As Int)
	EndIf
	
	;Debug.MessageBox("NumSpawns: " + numSpawns as String)
	
	;Debug.TraceUser("ASISLog", "After-Check numSpawns: " + numSpawns as String)
	If arSpawnRef.IsDead() == False
		Int currentElement = 0
		While (currentElement < numSpawns)
			;Actor temp = arSpawnRef.PlaceAtMe(newSpawn, 1, false, false) as Actor
			Actor temp = PlaceActorAtMe(newSpawn)
			temp.AddToFaction(uniqueSpawnFaction)
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
	;Grab all the values from the globals.
	multiplier = ASISSpawnMult.GetValue()
	Bool reducedInteriorSpawns = ASISUseInteriorSpawns.GetValue() As Bool
	If reducedInteriorSpawns == True && IsInInterior()
		interiorSpawns = ASISIntSpawnsReduceFactor.GetValue()
	Else
		interiorSpawns = 1
	EndIf
	
	spawn0weight = (ASISSpawnWeight0.GetValue() )
	spawn1weight = (ASISSpawnWeight1.GetValue() * multiplier / interiorSpawns) + spawn0weight
	spawn2weight = (ASISSpawnWeight2.GetValue() * multiplier / interiorSpawns) + spawn1weight
	spawn3weight = (ASISSpawnWeight3.GetValue() * multiplier / interiorSpawns) + spawn2weight
	spawn4weight = (ASISSpawnWeight4.GetValue() * multiplier / interiorSpawns) + spawn3weight
	spawn5weight = (ASISSpawnWeight5.GetValue() * multiplier / interiorSpawns) + spawn4weight
	spawn6weight = (ASISSpawnWeight6.GetValue() * multiplier / interiorSpawns) + spawn5weight
	spawn7weight = (ASISSpawnWeight7.GetValue() * multiplier / interiorSpawns) + spawn6weight
	spawn8weight = (ASISSpawnWeight8.GetValue() * multiplier / interiorSpawns) + spawn7weight
	spawn9weight = (ASISSpawnWeight9.GetValue() * multiplier / interiorSpawns) + spawn8weight
	
	;Debug.MessageBox("Spawn9Weight: " + spawn9weight as String)
	
	;Roll the random numbers.
	Float xSize = 0
	xSize = Utility.RandomFloat(1, spawn9weight)
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
	Return 0
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
	If IsInFaction(internalUniqueSpawnFaction)
		DeleteWhenAble()
	EndIf
EndEvent
