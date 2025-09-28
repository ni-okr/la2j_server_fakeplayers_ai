using UnrealBuildTool;
using System.Collections.Generic;

public class ModernLineage2Target : TargetRules
{
    public ModernLineage2Target(TargetInfo Target) : base(Target)
    {
        Type = TargetType.Game;
        DefaultBuildSettings = BuildSettingsVersion.V5;
        IncludeOrderVersion = EngineIncludeOrderVersion.Unreal5_6;
        CppStandard = CppStandardVersion.Cpp20;
        
        ExtraModuleNames.AddRange(new string[] { "ModernLineage2" });
    }
}

