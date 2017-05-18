/*
	Island Stage 1	- 1 
	Island Stage 2 	- 2
	Snow Stage 1 	- 3
	Snow Stage 2 	- 4
	Haunted Stage 1 - 5
	Haunted Stage 2 - 6
*/

var JavaPackages = new JavaImporter
(
	Packages.Tile
);
with(JavaPackages){
	function createStageGrid(stageSelection){
		switch(stageSelection){
			case 1:{ // Island Stage 1: 8x8 
				var grid = [
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()],
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()],
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()],
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()],
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()],
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()],
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()],
					[new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile(),new Tile()]
				]; 
				var gridArray = Java.to(grid, "Tile[][]");
				return gridArray; 
			}
			case 2: // Island Stage 2
			case 3: // Snow Stage 1
			case 4: // Snow Stage 2
			case 5: // Haunted Stage 1
			case 6: // Haunted Stage 2
		}
	}
}