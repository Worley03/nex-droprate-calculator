# Nex Droprate Calculator
This plugin monitors the players contribution of the nex boss fight and calculates the roll for a unique drop.

It helps to max the gear setup to succeed at the ffa worlds. It's also fun to see how dry you really go.

Here is a overview of the monitored properties:

- Unique Chance: The percentual chance to receive an unique drop
- Unique Roll: The roll that is made to receive an unique drop
- Contribution Per.: The percentual damage you have contributed to the fight
- Contribution Flat: The damage you have contributed to the fight and the total damage that was made by all players
- Contribution Min.: Indicates if enough damage was contributed to receive at least a normal drop
- Players: The amount of players that fight nex. Players will die or leave, so the number can decrease, to get an average idea of how many players participated
- Is MVP: Indicates if the player was MVP or not
- Time: The time it took to fight nex (This differs a bit from the chat time, because it monitors from model appear to model disappear)

In the average section, runs that were not cancel are calculated together to receive an average of the properties. It can be used to monitor how well a gear setup did over a course of time and on what performance you are likely to receive unique drops. We have some more properties here:
- Total Runs: The amount of runs that were calculated to an average
- MVP Total.: The total times the player was MVP
- Runs / h: A rough estimate how many runs you can do per hour, excluding banking and restock time, but includes respawn time.
- Uniques Chance /h: Based on Runs/h and unique Per., a calculation how high the cance for a drop after playing 1 hour is. Uses the complement rule to get a clearer result.

If you find any bugs, feel free to open an issue [here](https://github.com/MovEaxEax/nex-droprate-calculator/issues). But chances are high I won't realize it.