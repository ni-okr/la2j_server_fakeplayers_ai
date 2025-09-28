using UnrealBuildTool;
using System.Collections.Generic;

public class ModernLineage2EditorTarget : TargetRules
{
    public ModernLineage2EditorTarget(TargetInfo Target) : base(Target)
    {
        Type = TargetType.Editor;
        DefaultBuildSettings = BuildSettingsVersion.V5;
        IncludeOrderVersion = EngineIncludeOrderVersion.Unreal5_6;
        CppStandard = CppStandardVersion.Cpp20;
        bOverrideBuildEnvironment = true;
        
        ExtraModuleNames.AddRange(new string[] { "ModernLineage2" });
    }
}