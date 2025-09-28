#include "AdultContent.h"

#define LOCTEXT_NAMESPACE "FAdultContentModule"

void FAdultContentModule::StartupModule()
{
    UE_LOG(LogTemp, Warning, TEXT("AdultContent module started!"));
}

void FAdultContentModule::ShutdownModule()
{
    UE_LOG(LogTemp, Warning, TEXT("AdultContent module shutdown"));
}

#undef LOCTEXT_NAMESPACE

IMPLEMENT_MODULE(FAdultContentModule, AdultContent)