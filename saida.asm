dseg SEGMENT PUBLIC
	byte 4000h DUP(?)
	byte ?
dseg ENDS
cseg SEGMENT PUBLIC
ASSUME CS:cseg, DS:dseg
strt:
	mov AX, dseg
	mov DS, AX
	mov AX, DS:[4000]
	mov DS:[4000], AX
cseg ENDS
END strt
