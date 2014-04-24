;Skope Installer Script
;v3.0
;Adapted by Christian Gunderman

;--------------------------------
;Setup

!include "MUI2.nsh"
!ifdef HAVE_UPX 
!packhdr tmp.dat "upx\upx -9 tmp.dat"
!endif
SetCompressor /SOLID lzma

;--------------------------------
;Setup Java Downloader

!define JRE_VERSION "1.6"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=52252"
!include "Rsc\JREDyna_Inetc.nsh"

;--------------------------------
;General

  ;Name and file
  Name "Skope 3 ALPHA RC 3"
  Caption "Skope Surveilliance Suite"
  OutFile "SkopeInstall.exe"
  InstallColors FF0000 00FF00 ;Two colors
  Icon "Rsc\Skope.ico"
  XPStyle on
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\Gundersoft\Skope 3" ""
  
  ;Request application privileges for Windows
  RequestExecutionLevel admin
  
  

;--------------------------------
;Interface Settings

;Memento Settings
!define MEMENTO_REGISTRY_ROOT HKLM
!define MEMENTO_REGISTRY_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\Skope 3"

!define MUI_ABORTWARNING
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "Rsc\INST_BANNER.bmp" ; Banner
!define MUI_WELCOMEFINISHPAGE_BITMAP "Rsc\INST_WELCOME.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "Rsc\INST_UNWELCOME.bmp"
!define MUI_COMPONENTSPAGE_SMALLDESC
!define MUI_WELCOMEPAGE_TITLE "Welcome to the Skope 3 Setup Wizard"

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "..\LICENSE.txt"
  !insertmacro MUI_PAGE_COMPONENTS 
  ;!insertmacro MUI_PAGE_DIRECTORY
  !insertmacro CUSTOM_PAGE_JREINFO
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Language

!insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "Java Runtimes"
  call DownloadAndInstallJREIfNecessary
SectionEnd

Section "Skope Application"

  ;Define install path
  Var /GLOBAL InstPath

  ;Set to install to All Users dir
  SetShellVarContext all

  ;Default installation folder
  StrCpy $InstPath "$LOCALAPPDATA\Gundersoft\Skope 3"

  ;Set Current Directory
  SetOutPath "$InstPath"

  File "/oname=Skope3.jar" "..\Skope3.jar"
  CreateDirectory "$InstPath\pixmaps\default"
  SetOutPath "$InstPath\pixmaps\default"
  File "..\pixmaps\default\*.*"
  SetOutPath "$InstPath"
  File "..\LICENSE.txt"
  File "..\README.txt"
  File "..\Skope-3.ini"
  File "Rsc\Skope.ico"
 
  ;Set Skope System Password and Startup skope
  Exec 'javaw -jar "$InstPath\Skope3.jar" --pre-config' ;Start Skope Service
  
  ;Store installation folder
  WriteRegStr HKCU "Software\Gundersoft\Skope 3\InstDir" "" $InstPath

  ;Start Menu Shortcuts
  CreateDirectory "$SMPROGRAMS\Gundersoft"
  CreateShortCut "$SMPROGRAMS\Gundersoft\Skope 3.lnk" "$InstPath\Skope3.jar" "" "$InstPath\Skope.ico" ; use defaults for
  CreateShortCut "$SMPROGRAMS\Startup\Skope 3.lnk" "javaw" '-jar "$InstPath\Skope3.jar"' "$InstPath\Skope.ico"
  CreateShortCut "$SMPROGRAMS\Gundersoft\Uninstall.lnk" "$InstPath\Uninstall.exe" ; use defaults for

  ;Create uninstaller
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Skope3" "DisplayName" "Gundersoft Skope 3"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Skope3" "UninstallString" '"$InstPath\Uninstall.exe"'
  WriteUninstaller "$InstPath\Uninstall.exe"
  
  ;Create Startup Entry
  ;WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Run" "Skope3" 'javaw -jar "$InstPath\Skope3.jar"'

SectionEnd

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;Set to install to All Users dir
  SetShellVarContext all

  ;Default installation folder
  StrCpy $InstPath "$LOCALAPPDATA\Gundersoft\Skope 3"

  ;Set Current Directory
  SetOutPath "$InstPath"


  ;Delete Application Files
  DelAppFiles:

  Delete "$InstPath\Skope3.jar"

  ;If Skope is still running
  IfErrors SkopeRunning UninstOk
  SkopeRunning:
  MessageBox MB_ABORTRETRYIGNORE|MB_ICONSTOP "Unable to remove the Skope application files. Ensure that it is not running and try again." IDRETRY DelAppFiles
  Quit  
  UninstOk:

  ;Delete remaining files
  Delete "$InstPath\*.*"
  Delete "$InstPath\pixmaps\default\*.*"
  RMDir "$InstPath\pixmaps\default"
  RMDir "$InstPath\pixmaps"

  ;Delete Start Menu Entry
  Delete "$InstPath\Gundersoft\Skope 3.lnk"
  Delete "$InstPath\Startup\Skope 3.lnk"
  Delete "$InstPath\Gundersoft\Uninstall.lnk"
  RMDir "$InstPath\Gundersoft"

  ;Remove Uninstaller
  Delete "$InstPath\Uninstall.exe"
  
  ;Remove App folder
  RmDir "$SMPROGRAMS\Gundersoft"
  RMDir "$InstPath"

  DeleteRegKey /ifempty HKCU "Software\Gundersoft\Skope 3"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Gundersoft Skope 3"
  DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Run" "Skope3"

SectionEnd
