#!/bin/bash

# Script to fix common compilation errors in bot system

echo "Fixing bot compilation errors..."

# Fix Collection to List conversions
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/List<L2Object> knownObjects = _bot\.getKnownList()\.getKnownObjects()\.values();/List<L2Object> knownObjects = new ArrayList<>(_bot.getKnownList().getKnownObjects().values());/g' {} \;

# Fix getDistance calls with L2Character
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getDistance(other)/_bot.getDistance(other.getX(), other.getY(), other.getZ())/g' {} \;

# Fix getDistance calls with L2PcInstance
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getDistance(player)/_bot.getDistance(player.getX(), player.getY(), player.getZ())/g' {} \;

# Fix getDistance calls with L2Character in different contexts
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getDistance(ally)/_bot.getDistance(ally.getX(), ally.getY(), ally.getZ())/g' {} \;
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getDistance(threat)/_bot.getDistance(threat.getX(), threat.getY(), threat.getZ())/g' {} \;
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getDistance(_currentTarget)/_bot.getDistance(_currentTarget.getX(), _currentTarget.getY(), _currentTarget.getZ())/g' {} \;

# Fix getDistance calls with Point3D
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getDistance(_currentDestination)/_bot.getDistance(_currentDestination.getX(), _currentDestination.getY(), _currentDestination.getZ())/g' {} \;
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getDistance(_currentGuardPosition)/_bot.getDistance(_currentGuardPosition.getX(), _currentGuardPosition.getY(), _currentGuardPosition.getZ())/g' {} \;

# Fix getEffectList calls
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/character\.getEffectList()/character.getEffectList()/g' {} \;
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/ally\.getEffectList()/ally.getEffectList()/g' {} \;

# Fix isMember calls
find java/net/sf/l2j/gameserver/fakeplayer -name "*.java" -exec sed -i 's/_bot\.getParty()\.isMember(player)/_bot.getParty().isMember(player)/g' {} \;

echo "Bot errors fixed!"

