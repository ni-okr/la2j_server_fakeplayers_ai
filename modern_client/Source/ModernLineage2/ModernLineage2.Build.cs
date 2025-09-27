using UnrealBuildTool;

public class ModernLineage2 : ModuleRules
{
	public ModernLineage2(ReadOnlyTargetRules Target) : base(Target)
	{
		PCHUsage = PCHUsageMode.UseExplicitOrSharedPCHs;

		PublicDependencyModuleNames.AddRange(new string[] { 
			"Core", 
			"CoreUObject", 
			"Engine", 
			"InputCore",
			"UMG",
			"Slate",
			"SlateCore",
			"HeadMountedDisplay",
			"NavigationSystem",
			"AIModule",
			"GameplayTasks",
			"OnlineSubsystem",
			"OnlineSubsystemUtils",
			"Networking",
			"Sockets",
			"HTTP",
			"Json",
			"JsonUtilities"
		});

		PrivateDependencyModuleNames.AddRange(new string[] { 
			"Slate",
			"SlateCore",
			"ToolMenus",
			"EditorStyle",
			"EditorWidgets",
			"UnrealEd",
			"BlueprintGraph",
			"KismetCompiler",
			"PropertyEditor",
			"RenderCore",
			"RHI",
			"RenderCore",
			"ShaderCore",
			"AssetTools",
			"AssetRegistry",
			"ContentBrowser",
			"EditorWidgets",
			"EditorStyle",
			"ToolMenus"
		});

		// Enable adult content features
		PublicDefinitions.Add("ENABLE_ADULT_CONTENT=1");
		PublicDefinitions.Add("ENABLE_COSTUME_SYSTEM=1");
		PublicDefinitions.Add("ENABLE_SLAVE_TRADING=1");
		PublicDefinitions.Add("ENABLE_ADVENTURER_GUILD=1");
		
		// Linux specific settings
		if (Target.Platform == UnrealTargetPlatform.Linux)
		{
			PublicDefinitions.Add("PLATFORM_LINUX=1");
		}
	}
}
