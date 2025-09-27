using UnrealBuildTool;
using System.Collections.Generic;

public class ModernLineage2Target : TargetRules
{
	public ModernLineage2Target(TargetInfo Target) : base(Target)
	{
		Type = TargetType.Game;
		DefaultBuildSettings = BuildSettingsVersion.V2;
		ExtraModuleNames.AddRange(new string[] { "ModernLineage2" });
		
		// Linux specific optimizations
		if (Target.Platform == UnrealTargetPlatform.Linux)
		{
			bUsePCHFiles = true;
			bUseUnityBuild = false;
			bUseSharedPCHs = false;
			
			// Enable adult content for Linux builds
			GlobalDefinitions.Add("ENABLE_ADULT_CONTENT=1");
			GlobalDefinitions.Add("ENABLE_COSTUME_SYSTEM=1");
			GlobalDefinitions.Add("ENABLE_SLAVE_TRADING=1");
			GlobalDefinitions.Add("ENABLE_ADVENTURER_GUILD=1");
		}
	}
}
