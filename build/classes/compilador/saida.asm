dseg SEGMENT PUBLIC
	byte 4000h DUP(?)
	sword ?
	byte 256 DUP(?)
	byte ?
	byte 10
	byte 'Digite seu nome: '$
	 byte 256 DUP(?)
	byte 0
	byte 'Ola' '$
	byte 1
	byte?
dseg ENDS
cseg SEGMENT PUBLIC
ASSUME CS:cseg, DS,dseg
strt:
	mov AX, dseg
	mov DS, AX
	mov di, 4259
	mov cx, 0
	cmp ax,0
	jge R0
	mov bl, 2Dh
	mov ds:[di], bl
	add di, 1
	neg ax
R0:
	mov bx, 10
R1:
	add cx, 1
	mov dx, 0
	idiv bx
	push dx
	cmp ax, 0
	jne R1
R2:
	pop dx 
	add dx, 30h
	mov ds:[di],dl
	add di, 1
	add cx, -1
	cmp cx, 0
	jne R2
	mov dl, 024h
	mov ds:[di], dl
	mov dx, 4259
	mov ah, 09h
	int 21h
	mov  DX, 4279
	mov  AL, 0FFh
	mov  DS:[4279], AL
	mov  AH, 0Ah
	int  21h
	mov  AH, 02h
	mov  DL, 0Dh
	int  21h
	mov  DL, 0Ah
	int  21h
	mov  DX, 4279
	mov  AL, 0FFh
	mov  DS:[4279], AL
	mov  AH, 0Ah
	int  21h
	mov  AH, 02h
	mov  DL, 0Dh
	int  21h
	mov  DL, 0Ah
	int  21h
	mov DI, 4002
	mov SI, 4279
R0:
	mov AX, DS:[SI]
	mov DS:[4002], AX
	add DI, 1
	add SI, 1
	cmp AX, $
	je R1
	jmp R0
R1:
	mov AX, DS:[4534]
	mov DS:[4258], AX
	mov AX, DS:[4535]
	mov DS:[4000], AX
R2:
	mov di, 4536
	mov cx, 0
	cmp ax,0
	jge R3
	mov bl, 2Dh
	mov ds:[di], bl
	add di, 1
	neg ax
R3:
	mov bx, 10
R4:
	add cx, 1
	mov dx, 0
	idiv bx
	push dx
	cmp ax, 0
	jne R4
R5:
	pop dx 
	add dx, 30h
	mov ds:[di],dl
	add di, 1
	add cx, -1
	cmp cx, 0
	jne R5
	mov dl, 024h
	mov ds:[di], dl
	mov dx, 4536
	mov ah, 09h
	int 21h
	mov  AH, 02h
	mov  DL, 0Dh
	int  21h
	mov  DL, 0Ah
	int  21h
	mov di, 4002
	mov cx, 0
	cmp ax,0
	jge R3
	mov bl, 2Dh
	mov ds:[di], bl
	add di, 1
	neg ax
R3:
	mov bx, 10
R4:
	add cx, 1
	mov dx, 0
	idiv bx
	push dx
	cmp ax, 0
	jne R4
R5:
	pop dx 
	add dx, 30h
	mov ds:[di],dl
	add di, 1
	add cx, -1
	cmp cx, 0
	jne R5
	mov dl, 024h
	mov ds:[di], dl
	mov dx, 4002
	mov ah, 09h
	int 21h
	mov  AH, 02h
	mov  DL, 0Dh
	int  21h
	mov  DL, 0Ah
	int  21h
	mov AX, DS:[4000]
	mov BX, DS:[4544]
	add AX, BX
	mov DS:[4000], AX
	mov AX, DS:[4000]
	mov DS:[4000], AX
	mov AX, DS:[4000]
	mov BX, DS:[4259]
	cmp AX, BX
	jl R3
	mov CX, 0h
	jmp R4
R3:
	mov CX, FFh
R4:
	mov DS:[4545], CX
	mov AX, DS:[4545]
	mov DS:[4258], AX
	mov AX, DS:[4258]
	cmp AX, FFh
	jmp R2
cseg ENDS
END strt
