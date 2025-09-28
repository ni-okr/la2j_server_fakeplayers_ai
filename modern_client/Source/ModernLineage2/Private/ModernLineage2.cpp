#include "ModernLineage2.h"

#define LOCTEXT_NAMESPACE "FModernLineage2Module"

void FModernLineage2Module::StartupModule()
{
    UE_LOG(LogTemp, Warning, TEXT("Modern Lineage II module started!"));
}

void FModernLineage2Module::ShutdownModule()
{
    UE_LOG(LogTemp, Warning, TEXT("Modern Lineage II module shutdown"));
}

#undef LOCTEXT_NAMESPACE

IMPLEMENT_MODULE(FModernLineage2Module, ModernLineage2)