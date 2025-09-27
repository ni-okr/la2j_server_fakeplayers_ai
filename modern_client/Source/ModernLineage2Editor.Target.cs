using UnrealBuildTool;
using System.Collections.Generic;

public class ModernLineage2EditorTarget : TargetRules
{
	public ModernLineage2EditorTarget(TargetInfo Target) : base(Target)
	{
		Type = TargetType.Editor;
		DefaultBuildSettings = BuildSettingsVersion.V2;
		ExtraModuleNames.AddRange(new string[] { "ModernLineage2" });
		
		// Editor specific settings
		bBuildDeveloperTools = true;
		bUseChecksInShipping = true;
		
		// Linux specific optimizations
		if (Target.Platform == UnrealTargetPlatform.Linux)
		{
			bUsePCHFiles = true;
			bUseUnityBuild = false;
			bUseSharedPCHs = false;
			
			// Enable all features in editor
			GlobalDefinitions.Add("ENABLE_ADULT_CONTENT=1");
			GlobalDefinitions.Add("ENABLE_COSTUME_SYSTEM=1");
			GlobalDefinitions.Add("ENABLE_SLAVE_TRADING=1");
			GlobalDefinitions.Add("ENABLE_ADVENTURER_GUILD=1");
		}
	}
}
