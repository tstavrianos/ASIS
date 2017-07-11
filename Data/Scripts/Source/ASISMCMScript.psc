Scriptname ASISMCMScript extends SKI_ConfigBase  

; ===============================================================================================================================
; Name ...................: ASISMCMScript
; Description ............: SkyUI Mod Configuration Menu
; Author .................: lifestorock
; ===============================================================================================================================


; ===============================================================================================================================
; Script Update Maintance
; ===============================================================================================================================

int function GetVersion()
	return 1
endFunction

event OnVersionUpdate(int a_version)
	if (a_version > 1)
		Debug.Trace(self + ": Updating script to version " + a_version)
		OnConfigInit()
	endIf
endEvent


; ===============================================================================================================================
; Script Events & Functions
; ===============================================================================================================================

Event OnConfigInit()	

	Pages = new string[3]
	Pages[0] = "$ASIS_IncreasedSpawnPage"
	Pages[1] = "$ASIS_RandomSpawnPage"
	Pages[2] = "$ASIS_NPCPotionPage"	
	
	SetToggles()
EndEvent

Event OnPageReset(String a_page)

	if (a_page == "")
		LoadCustomContent("skyui/ASIS/ASIS.dds", 123, 143)
		return
	else
		UnloadCustomContent()
	endIf
	
	If (a_page == "$ASIS_IncreasedSpawnPage")
		SetCursorFillMode(TOP_TO_BOTTOM)
		AddHeaderOption("$ASIS_HeaderSpawnWeight")
		AddSliderOptionST("ISSpawnWeight0", "$ASIS_SliderSpawnWeight0", ASISSpawnWeight0.getvalue())
		AddSliderOptionST("ISSpawnWeight1", "$ASIS_SliderSpawnWeight1", ASISSpawnWeight1.getvalue())
		AddSliderOptionST("ISSpawnWeight2", "$ASIS_SliderSpawnWeight2", ASISSpawnWeight2.getvalue())
		AddSliderOptionST("ISSpawnWeight3", "$ASIS_SliderSpawnWeight3", ASISSpawnWeight3.getvalue())
		AddSliderOptionST("ISSpawnWeight4", "$ASIS_SliderSpawnWeight4", ASISSpawnWeight4.getvalue())
		AddSliderOptionST("ISSpawnWeight5", "$ASIS_SliderSpawnWeight5", ASISSpawnWeight5.getvalue())
		AddSliderOptionST("ISSpawnWeight6", "$ASIS_SliderSpawnWeight6", ASISSpawnWeight6.getvalue())
		AddSliderOptionST("ISSpawnWeight7", "$ASIS_SliderSpawnWeight7", ASISSpawnWeight7.getvalue())
		AddSliderOptionST("ISSpawnWeight8", "$ASIS_SliderSpawnWeight8", ASISSpawnWeight8.getvalue())
		AddSliderOptionST("ISSpawnWeight9", "$ASIS_SliderSpawnWeight9", ASISSpawnWeight9.getvalue())	
		SetCursorPosition(1)
		AddHeaderOption("$ASIS_HeaderOptions")
		AddToggleOptionST("ISReducedInterior", "$ASIS_ToggleISReducedInterior", ISInteriorVar)
	ElseIf (a_page == "$ASIS_RandomSpawnPage")
		SetCursorFillMode(TOP_TO_BOTTOM)
		AddHeaderOption("$ASIS_HeaderSpawnWeight")
		AddSliderOptionST("RSSpawnWeight0", "$ASIS_SliderSpawnWeight0", ASISRsSpawnWeight0.getvalue())
		AddSliderOptionST("RSSpawnWeight1", "$ASIS_SliderSpawnWeight1", ASISRsSpawnWeight1.getvalue())
		AddSliderOptionST("RSSpawnWeight2", "$ASIS_SliderSpawnWeight2", ASISRsSpawnWeight2.getvalue())
		AddSliderOptionST("RSSpawnWeight3", "$ASIS_SliderSpawnWeight3", ASISRsSpawnWeight3.getvalue())
		AddSliderOptionST("RSSpawnWeight4", "$ASIS_SliderSpawnWeight4", ASISRsSpawnWeight4.getvalue())
		AddSliderOptionST("RSSpawnWeight5", "$ASIS_SliderSpawnWeight5", ASISRsSpawnWeight5.getvalue())
		AddSliderOptionST("RSSpawnWeight6", "$ASIS_SliderSpawnWeight6", ASISRsSpawnWeight6.getvalue())
		AddSliderOptionST("RSSpawnWeight7", "$ASIS_SliderSpawnWeight7", ASISRsSpawnWeight7.getvalue())
		AddSliderOptionST("RSSpawnWeight8", "$ASIS_SliderSpawnWeight8", ASISRsSpawnWeight8.getvalue())
		AddSliderOptionST("RSSpawnWeight9", "$ASIS_SliderSpawnWeight9", ASISRsSpawnWeight9.getvalue())	
		SetCursorPosition(1)
		AddHeaderOption("$ASIS_HeaderOptions")
		AddToggleOptionST("RSUseInterior", "$ASIS_ToggleRSUseInterior", RSInteriorVar)
		AddSliderOptionST("RSRandomSpawnChance", "$ASIS_SliderRSRandomSpawnChance", ASISRsRandomSpawnChance.getvalue())
		AddSliderOptionST("RSMultiGroupChance", "$ASIS_SliderRSMultiGroupChance", ASISRsMultiGroupChance.getvalue())
	ElseIf (a_page == "$ASIS_NPCPotionPage")
		SetCursorFillMode(TOP_TO_BOTTOM)
		AddHeaderOption("$ASIS_HeaderOptions")
		AddSliderOptionST("NPCPotionItems", "$ASIS_SliderNPCPotionItems", ASISNPCPotionsnumItems.getvalue())
		AddSliderOptionST("NPCPotionChance", "$ASIS_SliderNPCPotionChance", ASISNPCPotionsnumChance.getvalue())
	EndIf
EndEvent

Function SetToggles()
	If ASISIntSpawns.getvalue() != 0
		ISInteriorVar = true
	Else
		ISInteriorVar = false
	EndIf
	
	If ASISRsUseInteriorSpawns.getvalue() != 0
		RSInteriorVar = true
	Else
		RSInteriorVar = false
	EndIf
EndFunction


; ===============================================================================================================================
; States: Increased Spawns
; ===============================================================================================================================

state ISSpawnWeight0
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight0.getvalue())
		SetSliderDialogDefaultValue(200)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight0.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(200)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight0")
	endEvent
endState

state ISSpawnWeight1
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight1.getvalue())
		SetSliderDialogDefaultValue(150)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight1.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(150)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight1")
	endEvent
endState

state ISSpawnWeight2
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight2.getvalue())
		SetSliderDialogDefaultValue(35)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight2.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(35)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight2")
	endEvent
endState

state ISSpawnWeight3
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight3.getvalue())
		SetSliderDialogDefaultValue(10)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight3.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(10)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight3")
	endEvent
endState

state ISSpawnWeight4
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight4.getvalue())
		SetSliderDialogDefaultValue(3)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight4.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(3)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight4")
	endEvent
endState

state ISSpawnWeight5
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight5.getvalue())
		SetSliderDialogDefaultValue(2)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight5.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(2)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight5")
	endEvent
endState

state ISSpawnWeight6
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight6.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight6.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight6")
	endEvent
endState

state ISSpawnWeight7
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight7.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight7.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight7")
	endEvent
endState

state ISSpawnWeight8
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight8.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight8.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight8")
	endEvent
endState

state ISSpawnWeight9
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISSpawnWeight9.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISSpawnWeight9.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoISSpawnWeight9")
	endEvent
endState

state ISReducedInterior
	event OnSelectST()
		ISInteriorVar = !ISInteriorVar
		SetToggleOptionValueST(ISInteriorVar)	
		
		If ISInteriorVar == True 
			ASISIntSpawns.setvalue(1.00)			
		Else
			ASISIntSpawns.setvalue(0.00)
		EndIf
	endEvent

	event OnDefaultST()
		ISInteriorVar = true
		SetToggleOptionValueST(ISInteriorVar)
		ASISIntSpawns.setvalue(1.00)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoSISReducedInterior")
	endEvent
endState


; ===============================================================================================================================
; States: Random Spawns
; ===============================================================================================================================

state RSSpawnWeight0
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight0.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight0.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight0")
	endEvent
endState

state RSSpawnWeight1
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight1.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight1.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight1")
	endEvent
endState

state RSSpawnWeight2
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight2.getvalue())
		SetSliderDialogDefaultValue(50)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight2.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(50)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight2")
	endEvent
endState

state RSSpawnWeight3
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight3.getvalue())
		SetSliderDialogDefaultValue(25)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight3.setvalue(value)
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(25)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight3")
	endEvent
endState

state RSSpawnWeight4
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight4.getvalue())
		SetSliderDialogDefaultValue(15)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight4.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(15)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight4")
	endEvent
endState

state RSSpawnWeight5
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight5.getvalue())
		SetSliderDialogDefaultValue(3)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight5.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(3)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight5")
	endEvent
endState

state RSSpawnWeight6
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight6.getvalue())
		SetSliderDialogDefaultValue(1)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight6.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(1)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight6")
	endEvent
endState

state RSSpawnWeight7
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight7.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight7.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight7")
	endEvent
endState

state RSSpawnWeight8
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight8.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight8.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight8")
	endEvent
endState

state RSSpawnWeight9
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsSpawnWeight9.getvalue())
		SetSliderDialogDefaultValue(0)
		SetSliderDialogRange(0, 400)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsSpawnWeight9.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(0)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSSpawnWeight9")
	endEvent
endState

state RSRandomSpawnChance
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsRandomSpawnChance.getvalue())
		SetSliderDialogDefaultValue(3)
		SetSliderDialogRange(0, 100)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsRandomSpawnChance.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(3)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRsRandomSpawnChance")
	endEvent
endState

state RSMultiGroupChance
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISRsMultiGroupChance.getvalue())
		SetSliderDialogDefaultValue(10)
		SetSliderDialogRange(0, 100)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISRsMultiGroupChance.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(10)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSMultiGroupChance")
	endEvent
endState

state RSUseInterior
	event OnSelectST()
		RSInteriorVar = !RSInteriorVar
		SetToggleOptionValueST(RSInteriorVar)	
		
		If RSInteriorVar == True 
			ASISRsUseInteriorSpawns.setvalue(1.00)			
		Else
			ASISRsUseInteriorSpawns.setvalue(0.00)
		EndIf
	endEvent

	event OnDefaultST()
		RSInteriorVar = true
		SetToggleOptionValueST(RSInteriorVar)
		ASISRsUseInteriorSpawns.setvalue(1.00)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoRSUseInterior")
	endEvent
endState


; ===============================================================================================================================
; States: NPC Potion
; ===============================================================================================================================

state NPCPotionItems
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISNPCPotionsnumItems.getvalue())
		SetSliderDialogDefaultValue(5)
		SetSliderDialogRange(0, 100)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISNPCPotionsnumItems.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(5)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoNPCPotionItems")
	endEvent
endState

state NPCPotionChance
	event OnSliderOpenST()
		SetSliderDialogStartValue(ASISNPCPotionsnumChance.getvalue())
		SetSliderDialogDefaultValue(10)
		SetSliderDialogRange(0, 100)
		SetSliderDialogInterval(1)
	endEvent

	event OnSliderAcceptST(float value)
		ASISNPCPotionsnumChance.setvalue(value) 
		SetSliderOptionValueST(value)
	endEvent

	event OnDefaultST()
		SetSliderOptionValueST(10)
	endEvent

	event OnHighlightST()
		SetInfoText("$ASIS_InfoNPCPotionChance")
	endEvent
endState


; ===============================================================================================================================
; Variables & Properties
; ===============================================================================================================================

bool ISInteriorVar = true
bool RSInteriorVar = false

;Increased Spawn
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
GlobalVariable Property ASISIntSpawns Auto 

;Random Spawn
GlobalVariable Property ASISRsSpawnWeight0 Auto 
GlobalVariable Property ASISRsSpawnWeight1 Auto 
GlobalVariable Property ASISRsSpawnWeight2 Auto 
GlobalVariable Property ASISRsSpawnWeight3 Auto 
GlobalVariable Property ASISRsSpawnWeight4 Auto 
GlobalVariable Property ASISRsSpawnWeight5 Auto 
GlobalVariable Property ASISRsSpawnWeight6 Auto 
GlobalVariable Property ASISRsSpawnWeight7 Auto 
GlobalVariable Property ASISRsSpawnWeight8 Auto 
GlobalVariable Property ASISRsSpawnWeight9 Auto 
GlobalVariable Property ASISRsRandomSpawnChance Auto 
GlobalVariable Property ASISRsMultiGroupChance Auto 
GlobalVariable Property ASISRsUseInteriorSpawns Auto 

;NPC Potions
GlobalVariable Property ASISNPCPotionsnumItems Auto 
GlobalVariable Property ASISNPCPotionsnumChance Auto 