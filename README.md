# CaptainWarp
**CaptainWarp 2.0**

This is a lot of text sorry

Requirements: PermissionsEx

**Configuration:** /plugins/CaptainWarp/config.yml

   - totalwarpsallowed : set the total warps allowed. Does not include homes.
    
   - createReadMe : if set to true, will place readme.txt in /plugins/CaptainWarp/ If you delete readme.txt it will be replaced unless you set this to false.

**Features:** A condensed rundown of things available

   - Public Warps:
    
        - Easily organize and manage public warps. 
        
        - Included permissions editing commands to make your life easier.
        
        - Permissions at every level, precise control of what your users can do.
        
   - Private Warps:
    
        - Allows players to create their own warps and manage access themselves (without direct access to any permissions).
        
        - Allows players to access their allowed warps easily.
        
   - Private Homes:
    
        - Allow players to create homes and manage access themselves.
        
        - Allow players to travel to their friends homes easily.
        

## Commands:

**/warp:**
   
   - \<blank>: 
        
        - Shows help menu. Shows commands available to the player based on their permissions
   - list:
   
        - Shows warps to player. Warps that the player has permission to access will be shown in green, warps that the player does not have access to will be shown in red

        - If player has captainwarp.* perms, will show all hidden warps in gray

        - If performed by console, will show all warps/homes/hidden warps

   - \<warp>:

        - Shows basic info of \<warp>

        - If player has captainwarp.*, captainwarp.info, or owns the warp, they can see all info and hidden warps

        - Other players cannot see hidden warps or permissions nodes

        - If the player has permission to visit the warp, they will see the warp the name/owner/location of the warp

        - If the player doesn't have permission to visit the warp, they will see the name/owner of the warp

        - If executed by console, shows all info
```python
# Private warps are intended for most players. 
# Warps created will be automatically assigned a permissions node (captainwarp.warps.<warpname>)
# Owners of warps can give/revoke access to players as they wish (regardless of their permissions)
# Owners of warps with captainwarp.private.rename can rename their own private warps
# Owners can update the location of their warps and remove them entirely
```

   - private \<warp>:

        - Requires permissions captainwarp.*, captainwarp.private, or captainwarp.create

        - Creates a private warp at the current location

   - allow \<warp> \<player>:

        - Requires permissions captainwarp.* or captainwarp.access

        - Does not require permissions if the player is the owner of the warp

        - Will work with homes, private, or public warps (although not intended for use with homes)

   - disallow \<warp> \<player>:

        - Requires permissions captainwarp.* or captainwarp.access

        - Does not require permissions if the player is the owner of the warp

        - Denies the specified player access to the specified warp

        - Will work with homes, private, or public warps (although not intended for use with homes)

   - remove \<warp>:

        - Removes warp specified if player has captainwarp.*, captainwarp.remove, OR is the owner of the warp

   - update \<warp>:

        - Requires permissions captainwarp.*, captainwarp.update

        - Does not require permissions if the player is the owner of the warp to be updated

        - Updates warp location to current location

        - Can update public or private warps

``` python
# The create command will create warps with custom permissions nodes (don't give this permission to just anyone)
# The node given can be the same as another warp, to allow easier setup for servers with multiple warps accessed by everyone
# The node can be the same as a private warp's, allowing whoever created the private warp to
# control access to every warp with that node
# For this reason it is recommended to set custom permissions nodes to things not allowed to be set by /warp private
# To do this easily, begin all custom warp nodes with @ (the only symbol restricted from
# private warp naming and therefore permission setting)
# Example: /warp create london @cities
#   /warp create newyork @cities
#   /warp create paris @cities
# This will allow all players with the captainwarp.warps.@cities permissions node to access all three warps
# It also prevents players from creating a private warp called 'cities' and controlling everybody's access to the warps
```

   - create \<warp> \<node>:

        - Requires permissions captainwarp.* or captainwarp.create
        
        - Intended for admins or similar. Creates a warp with the specified permissions node. Will be captainwarp.warps.\<node>

        - To create a hidden warp, start the name with an '@' symbol (requires captainwarp.* or captainwarp.override)

        - Hidden warps can be created with /warp private as well

        - Hidden warps will not show on /warp list unless player has captainwarp.* or is the owner of the warp

``` python
# Renaming a warp will only rename the warp
# It will not change the node, even if the warp is private
# This means that the /warp allow and /warp disallow commands will function regardless of the name of the warp
# To change nodes of warps and permissions, see the next two commands
```

   - rename \<warp> \<new name>:

        - Requires permissions captainwarp.* or captainwarp.rename or captainwarp.private.rename/captainwarp.create AND ownership of the specified warp

        - IMPORTANT: Unlike other private warp commands, rename still requires captainwarp.private.rename even if the player is the owner of the warp

        - IMPORTANT: Players with captainwarp.private.rename may only rename private warps that they own

        - IMPORTANT: Players with captainwarp.create may only rename warps that they own

        - Renames the warp to the new name, updating all players permissions to use the new name

``` python
# Renode is very powerful
# It can be used to change the node of a warp
# It can also give players the new node that had the old one, and take the old one away (if desired)
# This will iterate through all Permission Users on your server (if flag is 1/2), so large servers beware
```

   - renode \<warp> \<new node>:

        - Because of the native handling of private warps and homes, this command is intended for NONprivate and NONhome warps

        - It can still be used to change private warp nodes, but players with captainwarp.private permissions don't have access to this command

        - Requires permissions captainwarp.* or captainwarp.renode or captainwarp.create AND ownership of the specified warp

        - Changes the node of the warp to be the newly specified node (captainwarp.warps.\<newnode>)

        - To fix the problems caused by changing the node, run refactor after renode to fix user permissions

        - Example: /warp renode london @london

        - This will replace london's node with @london

        - The plugin will output the old node and new node if you want to run refactor after renode

``` python
# Refactor is intended to be run after/in conjunction with renode
# It is a way to easily update permissions without editing groups/users in the permissions.yml
# It is resource intensive and powerful
```

   - refactor \<old node> \<new node> \<groupFlag>:

        - Requires permissions captainwarp.* or captainwarp.refactor

        - Does not edit any warp properties, just user/group permissions

        - LIMITED CHECKING!!! Does not ensure that the original node was in use

        - Not compatible with home nodes

        - Group Flag Settings:
         
        - 0: Will iterate through groups and NOT users, adding the new node to groups that had the old node

        - 1: Will iterate through groups and NOT users, adding the new node to groups that had the old node AND removing the old node

        - 2: Will iterate through users and NOT groups, adding the new node to users that had the old node

        - 3: Will iterate through users and NOT groups, adding the new node to users that had the old node AND removing the old node

        - 4: Will iterate through groups AND users, adding the new node to those that had the old node

        - 5: Will iterate through groups AND users, adding the new node to those that had the old node AND removing the old node

        - Be cautious when running this command, abuse could lead to a messy permissions.yml

**/home:**
   - \<blank>:
   
        - If the player has no home set, shows help menu
        
        - If the player has a home set, will teleport player to their home
        
   - set:
   
        - Requires captainwarp.home.self or captainwarp.*

        - Sets the player's home to the player's current location
   - help:
   
        - Shows help menu.
   - list:

        - Requires captainwarp.home.list

        - Shows a list of player homes
   - \<player>:

        - Requires captainwarp.home.@\<player> or captainwarp.*

        - Teleports the player to the specified player's home

        - Intended for use with /home allow and /home disallow
   - allow \<player>:

        - Requires captainwarp.* or captainwarp.home.self

        - Gives the specified player permission to access the command sender's home

        - Will add permission captainwarp.home.@\<sender> to the specified player
   - disallow \<player>:

        - Requires captainwarp.* or captainwarp.home.self

        - Revokes the specified player's permission to access the command sender's home

        - Will remove permission captainwarp.home.@\<sender> from the specified player
