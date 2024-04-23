<script lang="ts">
    import {Chess} from 'chess.js';
    import type {Square} from 'chess.js';
    import {onMount} from "svelte";

    let chess = new Chess();
    let board = chess.board();
    $: message = "Hello welcome to chess-awesome";
    $: turn = chess.turn();

    type BoardPiece = string | null;
    type BoardArray = BoardPiece[][];
    $: boardArray = getBoardArray();

    type SelectedSquare = { x: number; y: number } | null;
    let selectedSquare: SelectedSquare = null;

    /* IMPLEMENT THIS WHEN FEN CAN BE FETCHED FROM BACKEND */

    /* onMount(async () => {
        await fetchFEN();
    });

    function loadFEN(fen: string) {
        chess.load(fen);
        board = chess.board();
        boardArray = getBoardArray();
        turn = chess.turn();
    }

    async function fetchFEN(): Promise<void> {
        try {
            const response = await fetch('/api/get_fen');
            if (!response.ok) {
                throw new Error('Failed to fetch FEN from backend');
            }

            const data = await response.json();
            loadFEN(data.fen);
        } catch (error) {
            console.error("Error fetching FEN from backend:", error);
            message = "Error fetching FEN from backend";
        }
    }
     */

    function getBoardArray(): BoardArray {
        return board.map(row => row.map(piece => piece ? piece.type + piece.color : null));
    }



    function selectSquare(x: number, y: number): void {
        const piece = chess.get(toAlgebraic(x, y));
        if (selectedSquare) {
            if (piece && toAlgebraic(selectedSquare.x, selectedSquare.y) === toAlgebraic(x, y)) {
                selectedSquare = null;
            } else {
                handleMove(selectedSquare, {x, y});
                selectedSquare = null;
                message = "";
            }
        } else {
            if (piece && (piece.color === chess.turn())) {
                selectedSquare = {x, y};
            } else {
                message = "It's not the turn of this piece or empty square selected";
            }
        }
    }

    function handleMove(from: SelectedSquare, to: { x: number; y: number }): void {
        if (from) {
            const move = chess.move({
                from: toAlgebraic(from.x, from.y),
                to: toAlgebraic(to.x, to.y)
            });
            if (move) {
                updateBoardState();
            } else {
                message = "Invalid move";
            }
        }
    }

    function updateBoardState(): void {
        board = chess.board();
        boardArray = getBoardArray();
        turn = chess.turn();
        const currentFEN = chess.fen();
        console.log("Current FEN:", currentFEN);
    }

    function toAlgebraic(x: number, y: number): Square {
        return ('abcdefgh'[x] + (8 - y)) as Square;
    }
</script>


<div class="flex flex-col w-full h-[1000px] justify-center items-center">
    <div class="text-2xl my-10">{turn === 'w' ? 'White' : 'Black'}'s turn</div>
    <div class="text-2xl my-10">{message}</div>
    <div class="grid grid-cols-8 w-[500px] h-[600px]">
        {#each boardArray as row, y}
            {#each row as cell, x}
                <div
                        role="button"
                        tabindex="0"
                        class="w-12 h-12 flex justify-center items-center {((x + y) % 2 === 0) ? 'bg-white' : 'bg-gray-800'} {selectedSquare !== null && selectedSquare.x === x && selectedSquare.y === y ? 'bg-blue-500' : ''}"
                        on:click={() => selectSquare(x, y)}
                        on:keydown={(e) => e.key === 'Enter' && selectSquare(x, y)}
                >
                    {cell === null ? '' : cell}
                </div>
            {/each}
        {/each}
    </div>
</div>
