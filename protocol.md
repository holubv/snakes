Protocol
====================================

Client bound (C <- S)
------------------------------------

**[C 0x00] Server status**

| name | field type | description |
|----|----|----|
| Protocol version | byte | |
| Players | short | |
| Max players | short | |
| Description | String | |

**[C 0x01] Login success**

| name | field type | description |
|----|----|----|
| Player id | short | |

**[C 0x02] Disconnect**

| name | field type | description |
|----|----|----|
| Reason | String | |

**[C 0x03] Player join**

| name | field type | description |
|----|----|----|
| Player id | short | |
| Player name | String | |

**[C 0x04] Player leave**

| name | field type | description |
|----|----|----|
| Player id | short | |

**[C 0x05] Player death**

| name | field type | description |
|----|----|----|
| Player id | short | |

**[C 0x06] Entity spawn**

| name | field type | description |
|----|----|----|
| Entity id | int | |
| Entity type | byte | 0 = snake |
| X | float | |
| Y | float | |
| Entity metadata | ... | |


| Snake metadata | |
|----|----|
| Player id | short |
| Speed | double |
| Color R | byte |
| Color G | byte |
| Color B | byte |


| Food metadata | |
|----|----|
| Type | byte |

**[C 0x07] Entity remove**

| name | field type | description |
|----|----|----|
| Entity id | int | |

**[C 0x08] Update Direction**

| name | field type | description |
|----|----|----|
| Entity id | int | |
| Direction | byte | |
| Head X | float | |
| Head Y | float | |

**[C 0x09] Snake tail**

| name | field type | description |
|----|----|----|
| Entity id | int | |
| Tail length | short | |
| Tail relative position | byte[] | 4 tails are 1 byte (00-00-00-00) |

Tail parts are encoded as following
```
Byte array

       1st tail         5th tail
            v             v
[ 00 00 00 00 , 00 00 00 00 , ...]
   ^             ^
 4th tail       8th tail


Relative coords (relative to the head or previous tail)
00 = [0, -1] up
11 = [0, +1] down
10 = [-1, 0] left
01 = [+1, 0] right
```

**[C 0x0A] Snake Tail Size Change**

| name | field type | description |
|----|----|----|
| Entity id | int | |
| Lambda | signed byte | currently works only +1 and -1 |

**[C 0x0B] Map Data**

| name | field type | description |
|----|----|----|
| Has bounds | boolean | |
| X | signed short | set if 'has bounds' |
| Y | signed short | set if 'has bounds' |
| width | short | set if 'has bounds' |
| height | short | set if 'has bounds' |

**[C 0x0C] ChatMessage**

| name | field type | description |
|----|----|----|
| Player id | short | sender |
| Message | String | |


Server bound (C -> S)
------------------------------------

**[S 0x00] Handshake**

| name | field type | description |
|----|----|----|
| Protocol version | byte | |
| Want login | boolean | false -> want status only |

**[S 0x01] Login**

| name | field type | description |
|----|----|----|
| Username | String | |
| Client version | String | |

**[S 0x02] Respawn request**

| name | field type | description |
|----|----|----|

Respawn is successful if server send *Entity spawn*
of snake entity with client's player id

**[S 0x03] Update Direction**

| name | field type | description |
|----|----|----|
| Entity id | int | |
| Direction | byte | |
| Head X | float | |
| Head Y | float | |

**[S 0x04] Chat**

| name | field type | description |
|----|----|----|
| Message | String | |

Connecting to the server
------------------------------------
1. C -> S **Handshake** with *Want login: true*
2. C -> S **Login** with player name
3. S -> C **Login success** with client's player id
4. S -> C **Map Data**






