#include "ModernLineage2.h"

#define LOCTEXT_NAMESPACE "FModernLineage2Module"

void FModernLineage2Module::StartupModule()
{
	// This code will execute after your module is loaded into memory; the exact timing is specified in the .uproject file
	UE_LOG(LogTemp, Warning, TEXT("Modern Lineage II Module Started"));
}

void FModernLineage2Module::ShutdownModule()
{
	// This function may be called during shutdown to clean up your module.  For modules that support dynamic reloading,
	// we call this function before unloading the module.
	UE_LOG(LogTemp, Warning, TEXT("Modern Lineage II Module Shutdown"));
}

#undef LOCTEXT_NAMESPACE
	
IMPLEMENT_MODULE(FModernLineage2Module, ModernLineage2)
