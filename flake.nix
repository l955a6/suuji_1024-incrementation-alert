{
  description = "development environment for suuji_1024-incrementation-alert";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        sbt = pkgs.sbt.override { jre = pkgs.temurin-jre-bin-25; };
      in
      {
        devShells = {
          default = pkgs.mkShell {
            buildInputs = [
              pkgs.temurin-bin-25
              sbt
              pkgs.metals
            ];

            shellHook = ''
              echo "Welcome to the suuji-1024-incrementation-monitor development environment!🤏"
            '';
          };
        };
      }
    );
}
