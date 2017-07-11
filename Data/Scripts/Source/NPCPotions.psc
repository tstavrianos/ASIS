Scriptname NPCPotions extends Actor  
{Script to process the addition of potions to NPC's.}

import Utility

bool property useNPCPotions auto
int property numItems auto
int property numChance auto
LeveledItem property potionsList1 auto
LeveledItem property potionsList2 auto
GlobalVariable property ASISNPCPotionsnumItems auto
GlobalVariable property ASISNPCPotionsnumChance auto

int xVar = 0
int yVar = 0

bool addedPotions = false

Event OnLoad()
	Debug.OpenUserLog("ASISLog")
	numChance = ASISNPCPotionsnumChance.getvalue() as Int
	numItems = ASISNPCPotionsnumItems.getvalue() as Int
	if addedPotions == false
		Debug.TraceUser("ASISLog", "Pre-wait")
		Debug.TraceUser("ASISLog", "Variables; NumChance= " + numChance + " and NumItems= " + numItems)
		wait(2)
		Debug.TraceUser("ASISLog", "Post-Wait")
		addPotions(self)
		Debug.TraceUser("ASISLog", "After addPotions")
		addedPotions = true
	endif
	Debug.CloseUserLog("ASISLog")
EndEvent

Function addPotions(Actor arSpawnRef)
	Debug.TraceUser("ASISLog", "Before dead check")
	if !arSpawnRef.IsDead()
	Debug.TraceUser("ASISLog", "arSpawnRef= " + arSpawnRef as String)
		Debug.TraceUser("ASISLog", "After dead check")
		int i = 0
		while (i < numItems)
			Debug.TraceUser("ASISLog", "Before random checks")
			xVar = Utility.RandomInt(0, 100)
			Debug.TraceUser("ASISLog", "x equals: " + x)
			if xVar <= numChance
				Debug.TraceUser("ASISLog", "Adding potion 1")
				arSpawnRef.AddItem(potionsList1, 1, false)
				Debug.TraceUser("ASISLog", "Added potion 1")
			endif
			yVar = Utility.RandomInt(0, 100)
			Debug.TraceUser("ASISLog", "x equals: " + x)
			if yVar <= numChance
				Debug.TraceUser("ASISLog", "Adding potion 2")
				arSpawnRef.AddItem(potionsList2, 1, false)
				Debug.TraceUser("ASISLog", "Added potion 2")
			endif
			i += 1
			Debug.TraceUser("ASISLog", "Counter: " + i)
		endwhile
	endif
endFunction
